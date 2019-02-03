package fi.jubic.easyvalue.processor;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

interface ProcessingResult<T> {
    Optional<T> result();
    List<ProcessingMessage> messages();

    static <T> ProcessingResult<T> of(
            @Nullable T result,
            List<ProcessingMessage> messages
    ) {
        return new ProcessingResult<T>() {
            @Override
            public Optional<T> result() {
                return Optional.ofNullable(result);
            }

            @Override
            public List<ProcessingMessage> messages() {
                return messages;
            }
        };
    }

    static <T> ProcessingResult<T> of(T result) {
        return of(result, Collections.emptyList());
    }

    static <T> ProcessingResult<T> of(List<ProcessingMessage> messages) {
        return of(null, messages);
    }

    static <T> ProcessingResult<T> of(ProcessingMessage message) {
        return of(null, Collections.singletonList(message));
    }
}
