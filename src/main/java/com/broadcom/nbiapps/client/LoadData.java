/**
 * 
 */
package com.broadcom.nbiapps.client;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * @author Balaji N
 *
 */
@Service
public class LoadData {
	

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		System.out.println("hello world, I have just started up");
		
	}
}
