package app.controller;

import app.util.Note;
import app.service.MessageSender;
import app.service.MessageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Controller;
import app.view.FormBean;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static app.util.QueueName.NAME;
import static app.util.CommonUtils.getStrongUniqueId;
import static app.util.CommonUtils.stackTraceAsString;

/**
 * author: Szymon Roziewski on 29.07.19 17:23
 * email: szymon.roziewski@gmail.com
 */

@Controller("formController")
@Scope("request")
@Slf4j
@Data
public class FormController {
	private final FormBean formBean;

	private final MessageSender messageSender;

	private final MessageService messageService;

	private String jmsQueueName = NAME;

	@Autowired
	public FormController(FormBean formBean, MessageSender messageSender, MessageService messageService) {
		this.formBean = formBean;
		this.messageSender = messageSender;
		this.messageService = messageService;
	}

	@SuppressWarnings("unchecked")
	public void submit() {
		String uniqueId = getStrongUniqueId();
		Note transferNote = new Note(formBean.getNote(), uniqueId);
	    formBean.getSubmittedValues().add(Map.entry(formBean.getNickname(), formBean.getMessage()));
		try {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Message submitted."));
		}
		catch (NullPointerException e){
			log.error(stackTraceAsString(e));
		}

		Future<Note> futureTask = new AsyncResult(sendMessageAsync(transferNote));

		waitForFuture(futureTask);
		messageService.waitForAdvices(uniqueId);
		if(futureTask.isDone()) {
			List<String> advices = messageService.getAdvices().remove(uniqueId);
			formBean.setAdvices(advices);
			formBean.refreshDataTable();
		}
		clear();
	}

	private void waitForFuture(Future<Note> taskFuture) {
		try {
			taskFuture.get();
		} catch (InterruptedException| ExecutionException e) {
			log.error(stackTraceAsString(e));
			e.printStackTrace();
		}
	}

	@Async
	public Note sendMessageAsync(Note transferNote){
		return messageSender.sendMessage(jmsQueueName, transferNote);
	}

	private void clear(){
		formBean.setNickname("");
		formBean.setMessage("");
	}

}
