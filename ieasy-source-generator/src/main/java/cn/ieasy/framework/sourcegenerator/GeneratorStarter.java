package cn.ieasy.framework.sourcegenerator;

import cn.ieasy.framework.sourcegenerator.impl.ControllerGenerator;
import cn.ieasy.framework.sourcegenerator.impl.ConvertorGenerator;
import cn.ieasy.framework.sourcegenerator.impl.DtoGenerator;
import cn.ieasy.framework.sourcegenerator.impl.RepositoryGenerator;
import cn.ieasy.framework.sourcegenerator.impl.ServiceGenerator;
import cn.ieasy.framework.sourcegenerator.impl.ServiceImplGenerator;
import cn.ieasy.framework.sourcegenerator.utils.Configuration;

public class GeneratorStarter {

    public static void main(final String[] args) throws ClassNotFoundException {
        if (args.length < 2) {
            System.out.println("请按照如下方式配置启动参数:");
            System.out.println("1. Model包路径: modelBasePackage=cn.ieasy.framework.sourcegenerator.model");
            System.out.println("2. Model数组，逗号分隔: models=User,Member,Role");
            System.out.println("3. 输出方式，默认控制台打印， file则输出到文件: output=file");
            System.out.println("4. 是否覆盖原文件，输出方式为file时才生效。默认不覆盖原文件: overrid=true");
            System.out.println("*****************参考配置*******************");
            System.out.println("modelBasePackage=cn.ieasy.framework.sourcegenerator.model");
            System.out.println("models=User,Member,Role");
            System.out.println("output=file");
            System.out.println("override=true");
            System.out.println("*****************参考配置*******************");
            return;
        }
        final String basePackage = args[0].trim().replace("modelBasePackage=", "");
        final String[] models = args[1].trim().replace("models=", "").split(",");
        String output = "console";
        if (args.length >= 3) {
            output = args[2].trim().replace("output=", "").trim();
            if (!"file".equals(output)) {
                output = "console";
            }
        }
        boolean override = false;
        if ("file".equals(output) && (args.length >= 4)) {
            final String overrideCommand = args[3].trim().replace("override=", "").trim();
            if ("true".equals(overrideCommand)) {
                override = true;
            }
        }

        final String outputDir = null;
        for (final String modelClass : models) {
            final Class<?> modelClazz = Class.forName(basePackage + "." + modelClass);
            final Configuration config = new Configuration(modelClazz, output, outputDir, override);
            final RepositoryGenerator repositoryGenerator = new RepositoryGenerator(config);
            final ServiceGenerator serviceGenerator = new ServiceGenerator(config);
            final ServiceImplGenerator serviceImplGenerator = new ServiceImplGenerator(config);
            final DtoGenerator dtoGenerator = new DtoGenerator(config);
            final ConvertorGenerator convertorGenerator = new ConvertorGenerator(config);
            final ControllerGenerator controllerGenerator = new ControllerGenerator(config);
            repositoryGenerator.generate();
            serviceGenerator.generate();
            serviceImplGenerator.generate();
            dtoGenerator.generate();
            convertorGenerator.generate();
            controllerGenerator.generate();
        }
    }
}
