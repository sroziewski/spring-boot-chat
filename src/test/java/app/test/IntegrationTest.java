package app.test;

import app.controller.FormController;
import app.service.ChatBot;
import app.service.MessageReceiver;
import app.service.MessageSender;
import app.service.MessageService;
import app.view.FormBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContextFacade;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.jms.ConnectionFactory;
import javax.servlet.ServletContext;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Slf4j
public class IntegrationTest {

	@Autowired
	private WebTestClient webClient;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private ConnectionFactory connectionFactory;

	@Autowired
	private ChatBot chatBot;

	private MockMvc mockMvc;

//	@Before
//	public void setup() {
//		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
//	}

	@Before
	public void setup() {
		MessageService messageService = new MessageService();
		mockMvc = MockMvcBuilders.standaloneSetup(new FormController(new FormBean(messageService), new MessageSender(connectionFactory), messageService),
				new MessageReceiver(connectionFactory, chatBot))
				.setHandlerExceptionResolvers(exceptionResolver())
				.setValidator(validator())
				.setViewResolvers(viewResolver())
				.build();
	}

	@Test
    public void mainFlowIntegrationTest(){
		Optional<FormController> formControllerOptional = mockMvc.getDispatcherServlet().getWebApplicationContext().getBeansOfType(FormController.class).entrySet().stream().map(Map.Entry::getValue).findFirst();
		assertTrue(formControllerOptional.isPresent());
		FormController formController = formControllerOptional.get();
		formController.getMessageSender().setJmsTemplate(new JmsTemplate(connectionFactory));
		MessageService messageService = formController.getMessageService();
		chatBot.setMessageService(messageService);
		FormBean formBean = formController.getFormBean();
		formBean.setNickname("nick");
		formBean.setMessage("head");
		formController.submit();
		Map.Entry<String, String> submittedNote = formController.getMessageService().getSubmittedValues().poll();
		assertEquals("nick", submittedNote.getKey());
		assertEquals("head", submittedNote.getValue());
		assertEquals(3, formBean.getFoundMessages().size());
		assertEquals("Life can be a lot more interesting inside your head.", formBean.getFoundMessages().get(1));
		assertEquals("", formBean.getNickname());
		assertEquals("", formBean.getMessage());
		log.info("mainFlowIntegrationTest OK");
    }


	@Test
	public void verifyTestConfiguration() {
		ServletContext servletContext = wac.getServletContext();
		assertNotNull(servletContext);
		assertTrue(servletContext instanceof ApplicationContextFacade);
		List.of("chatBot", "messageReceiver", "messageSender", "messageService")
			.forEach(name->assertNotNull(wac.getBean(name)));
		log.info("verifyTestConfiguration OK");
	}

	@Test
	public void test() {
		this.webClient.get().uri("/index.xhtml").exchange().expectStatus().isOk();
		log.info("index.xhtml OK");
	}

	private HandlerExceptionResolver exceptionResolver() {
		SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();

		Properties exceptionMappings = new Properties();

		exceptionMappings.put("some.exception", "error/404");
		exceptionMappings.put("java.lang.Exception", "error/error");
		exceptionMappings.put("java.lang.RuntimeException", "error/error");

		exceptionResolver.setExceptionMappings(exceptionMappings);

		Properties statusCodes = new Properties();

		statusCodes.put("error/404", "404");
		statusCodes.put("error/error", "500");

		exceptionResolver.setStatusCodes(statusCodes);

		return exceptionResolver;
	}

	private ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();

		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/jsp/");
		viewResolver.setSuffix(".jsp");

		return viewResolver;
	}

	private LocalValidatorFactoryBean validator() {
		return new LocalValidatorFactoryBean();
	}

}
