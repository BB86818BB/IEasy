package cn.ieasy.framework.sourcegenerator.impl;

import java.util.HashMap;
import java.util.Map;

import cn.ieasy.framework.sourcegenerator.AbstractGenerator;
import cn.ieasy.framework.sourcegenerator.ContentsFilter;
import cn.ieasy.framework.sourcegenerator.utils.Configuration;

public class ServiceGenerator extends AbstractGenerator {

    // 模板路径
    private static final String templatePath = "/codetemplate/Service.template";

    private ContentsFilter filter;

    private final String templateContents;

    public ServiceGenerator(final Configuration config) {
        super(config, "service");
        this.initFilter();
        this.templateContents = this.getFileString(ServiceGenerator.templatePath);
    }

    @Override
    public void generate() {
        final String value = this.filter.filter(this.templateContents);
        this.output(value);
    }

    private void initFilter() {

        final Map<String, String> serviceFilterMap = new HashMap<String, String>();
        serviceFilterMap.put("@Package@", this.getPackage("service"));
        serviceFilterMap.put("@ModelPath@", this.getModelPath());
        serviceFilterMap.put("@Model@", this.getModelName());
        this.filter = new ReplaceFilter(serviceFilterMap);
    }
}
