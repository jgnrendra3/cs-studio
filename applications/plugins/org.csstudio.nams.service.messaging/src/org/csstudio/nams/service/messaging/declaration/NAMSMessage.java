package org.csstudio.nams.service.messaging.declaration;

import java.util.Map;

import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.SystemNachricht;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;

/**
 * A message received from message service.
 * 
 * The message will be re-delivered until it has been successfully
 * {@linkplain #acknowledge() acknowledged}.
 * 
 * <p>
 * <strong>Achtung:</strong> Implementationen für JMS müssen in den Nachrichten
 * den "deliver type persistent" setzen!
 */
public interface NAMSMessage {
	public boolean enthaeltAlarmnachricht();

	public boolean enthaeltSystemnachricht();
	
	public AlarmNachricht alsAlarmnachricht();

	public SystemNachricht alsSystemachricht();
	
	@Deprecated
	public Map<String, String> alsMap();

	public String toString();

	/**
	 * <p>
	 * Indicates this message is handled successfully. The message will be
	 * re-delivered until this method has been successfully called on it.
	 * </p>
	 * 
	 * <p>
	 * NOTE: This should be done after all processing, like re-sending, have
	 * succeed.
	 * </p>
	 * 
	 * @throws MessagingException
	 *             If acknowledgment failed.
	 */
	public void acknowledge() throws MessagingException;
}
