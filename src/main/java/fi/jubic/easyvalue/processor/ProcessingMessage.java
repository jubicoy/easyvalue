package fi.jubic.easyvalue.processor;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ProcessingMessage {
    final Diagnostic.Kind kind;
    final CharSequence message;
    final Element valueElement;

    private ProcessingMessage(
            Diagnostic.Kind kind,
            CharSequence message,
            Element valueElement
    ) {
        this.kind = kind;
        this.message = message;
        this.valueElement = valueElement;
    }

    static ProcessingMessage of(
            Diagnostic.Kind kind,
            CharSequence message,
            Element valueElement
    ) {
        return new ProcessingMessage(kind, message, valueElement);
    }

    static List<ProcessingMessage> list(ProcessingMessage... messages) {
        return new ArrayList<>(Arrays.asList(messages));
    }
}
