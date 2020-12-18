package pl.alpaka.rentalapp.mvc.error;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import pl.alpaka.rentalapp.domain.user.User;
import pl.alpaka.rentalapp.domain.user.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/error")
@AllArgsConstructor
public class CustomErrorController implements ErrorController {
    private final UserService userService;

    @GetMapping
    public ModelAndView handleError(HttpServletRequest httpRequest){
        ModelAndView mav = new ModelAndView();
        String username = getUsername(mav);
        if(!username.equals("anonymousUser")){
            mav.setViewName("errorUser");
        }else {
            mav.setViewName("error");
        }

        System.out.println(getErrorMessage(httpRequest));
        String errorCode = httpRequest.getAttribute("javax.servlet.error.status_code").toString();
        String message = getMessageByErrorCode(errorCode);
        mav.addObject("errorCode", errorCode);
        mav.addObject("message", message);
        return mav;
    }

    private String getMessageByErrorCode(String errorCode) {
        String message = "";
        switch (Integer.valueOf(errorCode)) {
            case 400: {
                message = "Bad Request";
                break;
            }
            case 401: {
                message = "Unauthorized";
                break;
            }
            case 404: {
                message = "Resource not found";
                break;
            }
            case 500: {
                message = "Internal Server Error";
                break;
            }
        }
        return message;
    }

    private String getErrorMessage(HttpServletRequest httpRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append(LocalDateTime.now().toString());
        sb.append(" ERROR : ");
        sb.append(httpRequest.getAttribute("javax.servlet.error.status_code"));
        sb.append(" | ");
        sb.append(httpRequest.getAttribute("javax.servlet.error.message"));
        sb.append(httpRequest.getAttribute("javax.servlet.error.exception_type"));
        return sb.toString();
    }

    private String getUsername(ModelAndView mav) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if(!username.equals("anonymousUser")){
            mav.addObject("user", userService.getByUsername(username));
        }
        return username;
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute("javax.servlet.error.status_code");
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
