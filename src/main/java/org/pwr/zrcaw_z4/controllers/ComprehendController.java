package org.pwr.zrcaw_z4.controllers;

import org.pwr.zrcaw_z4.services.ComprehendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ComprehendController {

    @Autowired
    private ComprehendService comprehendService;

    @RequestMapping(value = "/detectLanguage", method = RequestMethod.GET)
    public String detectLanguage(Model model, @RequestParam(value = "input_text", defaultValue = "") String inputText) {

        model.addAttribute("inputText", inputText);
        model.addAttribute("resultText", comprehendService.detectLanguage(inputText));
        return "comprehend/detectLanguage";
    }

    @RequestMapping(value = "/detectSyntax", method = RequestMethod.GET)
    public String detectSyntax(Model model, @RequestParam(value = "input_text", defaultValue = "") String inputText) {

        model.addAttribute("inputText", inputText);
        model.addAttribute("tokens", comprehendService.detectAllSyntax(inputText));
        return "comprehend/detectSyntax";
    }

}
