package ascelion.micro.flow;

import ascelion.micro.shared.message.MessageSenderAdapter;

public abstract class AbstractSendTask<T> extends AbstractTask {
	protected final MessageSenderAdapter<T> msa;

	protected AbstractSendTask(MessageSenderAdapter<T> msa) {
		this.msa = msa;
	}
}
