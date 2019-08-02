package app.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import app.util.Note;
import app.service.MessageService;

@Component
@Scope("view")
public class FormBean {

	private final MessageService messageService;

	private Note note = new Note();
	private List<String> advices = new ArrayList<>();

	@Autowired
	public FormBean(MessageService messageService) {
		this.messageService = messageService;
	}

	public String getMessage() {
		return note.getMessage();
	}

	public Note getNote() {
		return note;
	}

	public String getNickname() {
		return note.getNickname();
	}

	public void setMessage(String note) {
		this.note.setMessage(note);
	}

	public void setNickname(String nickname) {
		this.note.setNickname(nickname);
	}

	public ConcurrentLinkedQueue<Map.Entry<String, String>> getSubmittedValues() {
		return messageService.getSubmittedValues();
	}

	public void setSubmittedValues(ConcurrentLinkedQueue<Map.Entry<String, String>> submittedValues) {
		this.messageService.setSubmittedValues(submittedValues);

	}

	public void setAdvices(List<String> advices) {
		this.advices = advices;
	}

	public List<String> getFoundMessages(){
		return advices;
	}

	public void refreshDataTable(){
	}
}
