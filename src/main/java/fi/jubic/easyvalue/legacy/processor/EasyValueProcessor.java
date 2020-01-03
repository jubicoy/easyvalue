package fi.jubic.easyvalue.legacy.processor;

import com.google.auto.service.AutoService;
import fi.jubic.easyvalue.legacy.EasyValue;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "fi.jubic.easyvalue.legacy.EasyValue",
        "fi.jubic.easyvalue.legacy.EasyProperty"
})
public class EasyValueProcessor extends AbstractProcessor {
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        DefinitionParser definitionParser = new DefinitionParser();
        ValueGenerator valueGenerator = new ValueGenerator(processingEnv);

        List<ProcessingResult<ValueDefinition>> definitionResults = roundEnvironment
                .getElementsAnnotatedWith(EasyValue.class)
                .stream()
                .map(definitionParser::parseValue)
                .collect(Collectors.toList());

        List<ProcessingMessage> messages = definitionResults.stream()
                .flatMap(result -> result.messages().stream())
                .collect(Collectors.toList());

        if (
                messages.stream().anyMatch(
                        message -> message.kind.equals(Diagnostic.Kind.ERROR)
                )
        ) {
            messages.forEach(message -> messager.printMessage(message.kind, message.message));
            return true;
        }

        List<ProcessingMessage> generationMessages = definitionResults.stream()
                .map(ProcessingResult::result)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(valueGenerator::generateValue)
                .map(ProcessingResult::messages)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Stream.of(
                messages.stream(),
                generationMessages.stream()
        )
                .flatMap(s -> s)
                .forEach(message -> messager.printMessage(message.kind, message.message));

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
