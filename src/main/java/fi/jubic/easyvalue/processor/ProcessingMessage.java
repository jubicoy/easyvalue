package fi.jubic.easyvalue.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.tools.Diagnostic;

class ProcessingMessage {
    final Diagnostic.Kind kind;
    final CharSequence message;

    private ProcessingMessage(Diagnostic.Kind kind, CharSequence message) {
        this.kind = kind;
        this.message = message;
    }

    static ProcessingMessage of(Diagnostic.Kind kind, CharSequence message) {
        return new ProcessingMessage(kind, message);
    }

    static List<ProcessingMessage> list(ProcessingMessage... messages) {
        return new ArrayList<>(Arrays.asList(messages));
    }
}
