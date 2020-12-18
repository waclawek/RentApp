package pl.alpaka.rentalapp.mvc.task;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.alpaka.rentalapp.domain.apartment.Apartment;
import pl.alpaka.rentalapp.domain.apartment.ApartmentService;
import pl.alpaka.rentalapp.domain.task.Task;
import pl.alpaka.rentalapp.domain.task.TaskService;
import pl.alpaka.rentalapp.domain.user.UserService;

import java.util.List;

@Controller
@RequestMapping("/owner/tasks")
@RequiredArgsConstructor
public class TasksControllerOwner {
    private final UserService userService;
    private final TaskService taskService;
    private final ApartmentService apartmentService;

    @GetMapping
    public ModelAndView getTaskPage() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        ModelAndView modelAndView = new ModelAndView("tasks.html");
        List<Task> tasks = taskService.getByUsername(username);
        modelAndView.addObject("tasks", tasks);
        List<Apartment> apartments = apartmentService.getAllByOwnerUsername(username);
        modelAndView.addObject("apartments", apartments);
        modelAndView.addObject("apartmentService", apartmentService);
        if (userService.getAll().stream().anyMatch(p -> p.getUsername().equals(username))) {
            modelAndView.addObject("user", userService.getByUsername(username));
        }
        return modelAndView;
    }

    @PostMapping
    @RequestMapping("/edit")
    ModelAndView editTask(@RequestParam(value = "id") Integer taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ModelAndView mav = new ModelAndView("editTask.html");

        Task taskToEdit = taskService.getOneById(taskId);
        List<Apartment> apartments = apartmentService.getAllByOwnerUsername(username);
        mav.addObject("name", username);
        mav.addObject("apartments", apartments);
        mav.addObject("apartmentService", apartmentService);
        mav.addObject("task", taskToEdit);
        if (userService.getByUsername(username).getUsername().equals(username)) {
            mav.addObject("user", userService.getByUsername(username));
            return mav;

        }
        return mav;
    }

    @PostMapping
    @RequestMapping("/save")
    String editTaskSaveChanges(@ModelAttribute Task task) {
        taskService.update(task);
        return "redirect:/tasks";
    }

    @GetMapping
    @RequestMapping("/create")
    ModelAndView createTask() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        ModelAndView mav = new ModelAndView("createTask.html");
        mav.addObject("name", username);
        mav.addObject("task", new Task());
        List<Apartment> apartments = apartmentService.getAllByOwnerUsername(username);
        mav.addObject("apartments", apartments);
        mav.addObject("apartmentService", apartmentService);
        if (userService.getAll().stream().anyMatch(p -> p.getUsername().equals(username))) {
            mav.addObject("user", userService.getByUsername(username));
            return mav;
        }
        return mav;
    }

    @PostMapping
    @RequestMapping("/createTask")
    String createTaskFinal(@ModelAttribute Task task) {
        taskService.create(task);
        return "redirect:/owner/tasks";
    }
}