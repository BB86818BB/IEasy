package cn.ieasy.framework.quartz.event;

import java.util.Date;

import org.springframework.context.ApplicationEvent;

public class StartAllJobsEvent extends ApplicationEvent {

    /**
     *
     */
    private static final long serialVersionUID = 3439249032052988722L;

    public StartAllJobsEvent() {
        super(new Date());
    }

}
