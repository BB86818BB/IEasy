package cn.ieasy.framework.common.web.support;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import cn.ieasy.framework.common.web.dto.LoggerDTO;
import cn.ieasy.framework.common.web.dto.result.ListResultDTO;
import cn.ieasy.framework.common.web.dto.result.ResultDTO;

/**
 * Controller for view and managing Log Level at runtime.
 */
@RestController
@RequestMapping("/management")
public class LogsResource {

    @RequestMapping(value = "/logs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ListResultDTO<LoggerDTO> getList() {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        final List<LoggerDTO> rs = context.getLoggerList()
                .stream()
                .map(LoggerDTO::new)
                .collect(Collectors.toList());
        return ListResultDTO.success(rs);
    }

    @RequestMapping(value = "/logs", method = RequestMethod.PUT)
    @Timed
    public ResultDTO<Void> changeLevel(@RequestBody final LoggerDTO jsonLogger) {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
        return ResultDTO.success();
    }
}
