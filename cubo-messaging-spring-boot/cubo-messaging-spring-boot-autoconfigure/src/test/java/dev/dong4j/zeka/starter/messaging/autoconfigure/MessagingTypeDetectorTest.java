package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.starter.messaging.annotation.MessagingListener;
import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import dev.dong4j.zeka.starter.messaging.util.MessagingTypeDetector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessagingTypeDetectorTest {

    @Mock
    private Environment environment;

    @Test
    void shouldDetectSingleMQType() {
        MessagingTypeDetector detector = new MessagingTypeDetector(environment);
        detector.detectAvailableTypes();

        assertTrue(detector.hasSingleType());
        assertEquals(MessagingType.KAFKA, detector.getDefaultType());
    }

    @Test
    void shouldRequireExplicitTypeWhenMultipleMQAvailable() {
        MessagingTypeDetector detector = new MessagingTypeDetector(environment);
        detector.detectAvailableTypes();

        assertFalse(detector.hasSingleType());
        assertNull(detector.getDefaultType());

        // 验证注解配置
        MessagingListener annotation = mock(MessagingListener.class);
        when(annotation.type()).thenReturn(MessagingType.DEFAULT);

        assertThrows(IllegalStateException.class, () -> detector.validate(annotation));
    }

    @Test
    void shouldResolveConfiguredDefaultType() {
        // 配置默认类型
        when(environment.getProperty("unified.mq.default-type", MessagingType.class))
            .thenReturn(MessagingType.ROCKETMQ);

        MessagingTypeDetector detector = new MessagingTypeDetector(environment);
        MessagingType resolved = detector.resolveType(MessagingType.DEFAULT);

        assertEquals(MessagingType.ROCKETMQ, resolved);
    }
}
