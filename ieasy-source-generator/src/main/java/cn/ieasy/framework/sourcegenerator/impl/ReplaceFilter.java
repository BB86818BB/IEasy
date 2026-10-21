package cn.ieasy.framework.sourcegenerator.impl;

import java.util.Map;

import cn.ieasy.framework.sourcegenerator.ContentsFilter;

public class ReplaceFilter implements ContentsFilter {

    Map<String, String> replaceMap;

    public ReplaceFilter(final Map<String, String> replaceMap) {
        this.replaceMap = replaceMap;
    }

    public String filter(final String contents) {
        String result = contents;
        for (final String regex : this.replaceMap.keySet()) {
            final String replacement = this.replaceMap.get(regex);
            result = result.replaceAll(regex, replacement);
        }
        return result;
    }
}
