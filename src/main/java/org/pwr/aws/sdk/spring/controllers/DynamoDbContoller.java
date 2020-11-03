package org.pwr.aws.sdk.spring.controllers;

import org.pwr.aws.sdk.spring.dtos.CreateTable;
import org.pwr.aws.sdk.spring.models.Note;
import org.pwr.aws.sdk.spring.services.NotesDynamoDbService;
import org.pwr.aws.sdk.spring.services.TablesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DynamoDbContoller {
    @Autowired
    private NotesDynamoDbService notesDynamoDbService;
    @Autowired
    private TablesService tablesService;

    @RequestMapping(value = "/dynamodb", method = RequestMethod.GET)
    public String displayAll(Model model) {
        model.addAttribute("notes", notesDynamoDbService.getAll());
        model.addAttribute("tables", tablesService.getAllTables());
        return "dynamodb/main";
    }

    @RequestMapping(value = "/dynamodb/notes/{uuid}/delete", method = RequestMethod.GET)
    public String deleteNote(@PathVariable(value = "uuid") String uuid) {
        notesDynamoDbService.removeNote(uuid);
        return "redirect:/dynamodb";
    }

    @RequestMapping(value = "/dynamodb/notes/{uuid}/edit", method = RequestMethod.GET)
    public String editNote(@PathVariable(value = "uuid") String uuid, Model model) {
        model.addAttribute("note", notesDynamoDbService.getNoteByUuid(uuid));
        return "dynamodb/edit_note";
    }

    @RequestMapping(value = "/dynamodb/notes/{uuid}/edit", method = RequestMethod.POST)
    public String saveEditNote(@PathVariable(value = "uuid") String uuid, @ModelAttribute("note") Note note) {
        notesDynamoDbService.updateNote(uuid, note);
        return "redirect:/dynamodb";
    }

    @RequestMapping(value = "/dynamodb/notes/create", method = RequestMethod.GET)
    public String createNote(Model model) {
        model.addAttribute("note", new Note());
        return "dynamodb/new_note";
    }

    @RequestMapping(value = "/dynamodb/notes", method = RequestMethod.POST)
    public String saveCreateNote(@ModelAttribute("note") Note note) {
        notesDynamoDbService.addNote(note);
        return "redirect:/dynamodb";
    }

    @RequestMapping(value = "/dynamodb/tables/create", method = RequestMethod.GET)
    public String createTable(Model model) {
        model.addAttribute("table", new CreateTable());
        return "dynamodb/new_table";
    }

    @RequestMapping(value = "/dynamodb/tables", method = RequestMethod.POST)
    public String saveCreateTable(@ModelAttribute("table") CreateTable table, RedirectAttributes redirectAttributes) {
        if (tablesService.isTableCreated(table.getName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Table with this name already exists!");
            return "redirect:/dynamodb/tables/create";
        } else if (table.getName().isEmpty() || table.getKey().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fill in all inputs!");
            return "redirect:/dynamodb/tables/create";
        } else {
            tablesService.createTable(table.getName(), table.getKey());
        }
        return "redirect:/dynamodb";
    }

}
