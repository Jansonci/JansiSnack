package tech.wetech.api.common;


/**
 * @author Jansonci
 */
public interface EventStore {

  void append(DomainEvent aDomainEvent);

}
