package com.developmentsprint.spring.breaker;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.developmentsprint.spring.breaker.CircuitManager;
import com.developmentsprint.spring.breaker.CircuitManager.Invoker;
import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttribute;
import com.developmentsprint.spring.breaker.interceptor.DefaultCircuitBreakerAttribute;
import com.developmentsprint.spring.breaker.test.AnnotatedFooBarInterface;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:context.xml")
@ActiveProfiles({ "annotationDriven", "mockManager" })
public class AnnotationDrivenInterfaceTest {

    @Autowired
    private AnnotatedFooBarInterface methods;

    @Autowired
    private CircuitManager mockCircuitManager;

    @Before
    public void setup() {
        when(mockCircuitManager.execute(any(Invoker.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return ((Invoker) invocation.getArguments()[0]).invoke();
            }
        });
    }

    @After
    public void reset() {
        Mockito.reset(mockCircuitManager);
    }

    @Test
    public void testUnguarded() throws Exception {
        methods.getCount();
        verifyZeroInteractions(mockCircuitManager);
    }

    @Test
    public void testUnguarded2() throws Exception {
        methods.getDescription();
        verifyZeroInteractions(mockCircuitManager);
    }

    @Test
    public void testGuarded() throws Exception {

        ArgumentCaptor<Invoker> invokerCaptor = ArgumentCaptor.forClass(Invoker.class);

        methods.getName();

        verify(mockCircuitManager, times(1)).execute(invokerCaptor.capture());

        Invoker actualInvoker = invokerCaptor.getValue();
        CircuitBreakerAttribute cbAttribute = actualInvoker.getCircuitBreakerAttribute();

        assertThat(cbAttribute).isNotNull();
        assertThat(cbAttribute).isInstanceOf(DefaultCircuitBreakerAttribute.class);
        assertThat(cbAttribute.getName()).isEqualTo("AnnotatedInterfaceGetNameMethodGuard");
        assertThat(cbAttribute.getProperties()).hasSize(0);
    }

    @Test
    public void testGuarded2() throws Exception {

        ArgumentCaptor<Invoker> invokerCaptor = ArgumentCaptor.forClass(Invoker.class);

        methods.getCreatedDate();

        verify(mockCircuitManager, times(1)).execute(invokerCaptor.capture());

        Invoker actualInvoker = invokerCaptor.getValue();
        CircuitBreakerAttribute cbAttribute = actualInvoker.getCircuitBreakerAttribute();

        assertThat(cbAttribute).isNotNull();
        assertThat(cbAttribute).isInstanceOf(DefaultCircuitBreakerAttribute.class);
        assertThat(cbAttribute.getName()).isEqualTo("AnnotatedInterfaceGetCreatedDateMethodGuard");
        assertThat(cbAttribute.getProperties()).hasSize(0);
    }
}
