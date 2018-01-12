package org.jasig.cas.ticket.registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisTicketRegistry extends AbstractDistributedTicketRegistry implements DisposableBean {
	
	private final static String TICKET_PREFIX = "TICKETGRANTINGTICKET:";

	/**
	 * redis client
	 */
	@NotNull
	private final RedisTemplate<String, Object> redisClient;
	
//	private final RedisClientUtil redisClientUtil;

	/**
	 * TGT cache entry timeout in seconds.
	 */
	@Min(0)
	private final long TGTTimeout;

	/**
	 * ST cache entry timeout in seconds.
	 */
	@Min(0)
	private final long STTimeout;

	/**
	 * Creates a new instance using the given redis client instance, which is
	 * presumably configured via
	 * <code>net.spy.redis.spring.redisClientFactoryBean</code>.
	 * 
	 * @param client
	 *            redis client.
	 * @param ticketGrantingTicketTimeOut
	 *            TGT timeout in seconds.
	 * @param serviceTicketTimeOut
	 *            ST timeout in seconds.
	 */
	public RedisTicketRegistry(final RedisTemplate<String, Object> client, final int ticketGrantingTicketTimeOut,
			final int serviceTicketTimeOut) {
		this.TGTTimeout = ticketGrantingTicketTimeOut;
		this.STTimeout = serviceTicketTimeOut;
		this.redisClient = client;
	}

	@Override
	public void addTicket(Ticket ticket) {
		logger.debug("Adding ticket {}", ticket);
		try {
			this.redisClient.boundValueOps(TICKET_PREFIX + ticket.getId()).set(ticket, getTimeout(ticket),
					TimeUnit.SECONDS);
		} catch (final Exception e) {
			logger.error("Failed adding {}", ticket, e);
		}

	}

	@Override
	public Ticket getTicket(String ticketId) {
		try {
			final Ticket t = (Ticket) this.redisClient.boundValueOps(TICKET_PREFIX + ticketId).get();
			if (t != null) {
				return getProxiedTicketInstance(t);
			}
		} catch (final Exception e) {
			logger.error("Failed fetching {} ", ticketId, e);
		}
		return null;

	}

	@Override
	public boolean deleteTicket(String ticketId) {
		logger.debug("Deleting ticket {}", ticketId);
		try {
			this.redisClient.delete(TICKET_PREFIX + ticketId);
			return true;
		} catch (final Exception e) {
			logger.error("Failed deleting {}", ticketId, e);
		}
		return false;

	}

	@Override
	public Collection<Ticket> getTickets() {
		Set<Ticket> tickets = new HashSet<Ticket>();
		Set<String> keys = this.redisClient.keys(TICKET_PREFIX + "*");
		for (String key : keys) {
			Ticket ticket = (Ticket) this.redisClient.boundValueOps(TICKET_PREFIX + key).get();
			if (ticket == null) {
				this.redisClient.delete(TICKET_PREFIX + key);
			} else {
				tickets.add(ticket);
			}
		}
		return tickets;

	}

	@Override
	protected void updateTicket(Ticket ticket) {
		logger.debug("Updating ticket {}", ticket);
		try {
			this.redisClient.boundValueOps(TICKET_PREFIX + ticket.getId()).set(ticket, getTimeout(ticket),
					TimeUnit.SECONDS);
		} catch (final Exception e) {
			logger.error("Failed updating {}", ticket, e);
		}

	}

	@Override
	protected boolean needsCallback() {
		return true;
	}

	private long getTimeout(final Ticket t) {
		if (t instanceof TicketGrantingTicket) {
			return this.TGTTimeout;
		} else if (t instanceof ServiceTicket) {
			return this.STTimeout;
		}
		throw new IllegalArgumentException("Invalid ticket type");
	}

	public void destroy() throws Exception {
		// do nothing
	}

}
