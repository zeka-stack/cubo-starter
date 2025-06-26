package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.starter.messaging.adapter.AbstractMessagingListenerAdapter;
import dev.dong4j.zeka.starter.messaging.annotation.MessagingListener;
import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.registry.MessagingListenerRegistry;
import dev.dong4j.zeka.starter.messaging.registry.MessagingRegistrationHandler;
import dev.dong4j.zeka.starter.messaging.util.MessagingTypeDetector;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = TestMQConfig.class)
class MessagingListenerRegistryTest {

    @Mock
    private MessagingRegistrationHandler registrationHandler;

    @Mock
    private MessagingTypeDetector typeDetector;

    private MessagingListenerRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new MessagingListenerRegistry(registrationHandler, typeDetector);
    }

    @Test
    void shouldRegisterListenerMethod() {
        // 模拟Bean和方法
        Object testBean = new Object();
        Method method = getTestMethod();

        // 模拟注解
        MessagingListener annotation = mock(MessagingListener.class);
        when(annotation.topic()).thenReturn("test-topic");
        when(annotation.groupId()).thenReturn("test-group");
        when(annotation.type()).thenReturn(MessagingType.KAFKA);

        // 模拟类型检测
        when(typeDetector.resolveType(any())).thenReturn(MessagingType.KAFKA);

        // 执行测试
        registry.registerMethod(testBean, method, annotation);

        // 验证注册调用
        ArgumentCaptor<AbstractMessagingListenerAdapter> adapterCaptor =
            ArgumentCaptor.forClass(AbstractMessagingListenerAdapter.class);

        verify(registrationHandler).registerAdapter(adapterCaptor.capture(), eq(annotation));

        AbstractMessagingListenerAdapter adapter = adapterCaptor.getValue();
        assertNotNull(adapter);
    }

    private Method getTestMethod() {
        try {
            return this.getClass().getMethod("testMethod");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    // 测试方法签名
    public void testMethod(UnifiedMessage message) {
        // 空方法用于测试
    }
}
