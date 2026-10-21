package cn.ieasy.framework.common.web.view.example;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/poc/excel/example")
public class ExcelViewForPocController {
    
    @RequestMapping(name = "/get", method = RequestMethod.GET)
    public ModelAndView get() {

        //dummy data
        final Map<String, String> revenueData = new HashMap<String, String>();
        revenueData.put("Jan-2010", "$100,000,000");
        revenueData.put("Feb-2010", "$110,000,000");
        revenueData.put("Mar-2010", "$130,000,000");
        revenueData.put("Apr-2010", "$140,000,000");
        revenueData.put("May-2010", "$200,000,000");

        //return excel view using BeanNameViewResolver
        return new ModelAndView("excelViewForPoc", "revenueData", revenueData);
    }
}
