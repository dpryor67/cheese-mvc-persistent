package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;


@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    MenuDao menuDao;

    @Autowired
    CheeseDao cheeseDao;


    @RequestMapping(value = "")
    public String menu(Model model){

        model.addAttribute("title", "Menu");
        model.addAttribute("menus", menuDao.findAll());
        return "menu/index";

    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {

        model.addAttribute(new Menu());
        model.addAttribute("title", "Add Menu");

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(Model model, @ModelAttribute @Valid Menu menu, Errors errors) {

        if (errors.hasErrors()){
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("title", menu.getName());
        model.addAttribute("menu", menu);
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId) {


        Menu menu = menuDao.findOne(menuId);
        AddMenuItemForm addMenuItemForm = new AddMenuItemForm(menu, cheeseDao.findAll());
        model.addAttribute("form", addMenuItemForm);
        model.addAttribute("title", "Add item to menu: " + menu.getName());

        return "menu/add-item";

    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.POST)
    public String addItem(Model model, @ModelAttribute @Valid AddMenuItemForm addMenuItemForm, Errors errors) {

        if (errors.hasErrors()){
            model.addAttribute("title", "Add item to menu: ");
            return "menu/add-item";
        }

        Cheese theCheese = cheeseDao.findOne(addMenuItemForm.getCheeseId());

        Menu theMenu = menuDao.findOne(addMenuItemForm.getMenuId());

        theMenu.addItem(theCheese);

        menuDao.save(theMenu);

        return "redirect:/menu/view/" + theMenu.getId();

    }

}
