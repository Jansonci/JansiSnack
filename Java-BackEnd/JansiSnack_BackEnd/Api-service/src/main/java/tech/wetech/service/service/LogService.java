package tech.wetech.service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.wetech.service.Admin3Properties;
import tech.wetech.api.common.CollectionUtils;
import tech.wetech.api.common.JsonUtils;
import tech.wetech.api.common.StringUtils;
import tech.wetech.api.dto.LogDTO;
import tech.wetech.api.dto.PageDTO;
import tech.wetech.api.model.StoredEvent;
import tech.wetech.service.repository.StoredEventRepository;


import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * @author Jansonci
 */
// 事件日志
@Service
public class LogService {

  private final StoredEventRepository storedEventRepository;

  private final Admin3Properties admin3Properties;

  public LogService(StoredEventRepository storedEventRepository, Admin3Properties admin3Properties) {
    this.storedEventRepository = storedEventRepository;
    this.admin3Properties = admin3Properties;
  }

  public PageDTO<LogDTO> findLogs(Set<String> typeNames, Pageable pageable) {
    Map<String, Admin3Properties.Event> eventProps = admin3Properties.getEvents();
    Page<StoredEvent> page = storedEventRepository.findByTypeNameInOrderByOccurredOnDesc(
      CollectionUtils.isEmpty(typeNames) ? eventProps.keySet() : typeNames,
      pageable
    );
    return new PageDTO<>(page.getContent().stream()
      .map(e -> new LogDTO(e.getId(),
          StringUtils.simpleRenderTemplate(eventProps.get(e.getTypeName()).getLogTemplate(), JsonUtils.parseToMap(e.getEventBody())),
          e.getEventBody(),
          e.getTypeName(),
          e.getOccurredOn(),
          e.getUser()
        )
      )
      .collect(Collectors.toList()), page.getTotalElements());
  }

  public void cleanLogs() {
    storedEventRepository.deleteAll();
  }
}
