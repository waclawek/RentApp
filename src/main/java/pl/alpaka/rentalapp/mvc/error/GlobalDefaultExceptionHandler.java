//package pl.alpaka.rentalapp.mvc.error;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.servlet.ModelAndView;
//import pl.alpaka.rentalapp.domain.user.UserService;
//
//import javax.servlet.http.HttpServletRequest;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping(value = "/error2")
//public class GlobalDefaultExceptionHandler {
//
//    private final UserService userService;
//
//    @GetMapping()
////    @ExceptionHandler(value = Exception.class)
//    public ModelAndView
//    defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
//
//        if (AnnotationUtils.findAnnotation (e.getClass(), ResponseStatus.class) != null)
//            throw e;
//
//        // Otherwise setup and send the user to a default error-view.
//        String message = "";
//        int httpErrorCode = getErrorCode(req);
//        switch (httpErrorCode) {
//            case 400: {
//                message = "Bad Request";
//                break;
//            }
//            case 401: {
//                message = "Unauthorized";
//                break;
//            }
//            case 404: {
//                message = "Resource not found";
//                break;
//            }
//            case 500: {
//                message = "Internal Server Error";
//                break;
//            }
//        }
//        ModelAndView mav = new ModelAndView();
//        mav.addObject("message", message);
//        mav.addObject("errorCode", String.valueOf(httpErrorCode));
////        getUsername(mav);
//        mav.setViewName("error2");
//        return mav;
//    }
//
//    private void getUsername(ModelAndView mav) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        if (userService.getByUsername(username).getUsername().equals(username))
//            mav.addObject("user", userService.getByUsername(username));
//    }
//
//    private int getErrorCode(HttpServletRequest httpRequest) {
//        return (Integer) httpRequest
//                .getAttribute("javax.servlet.error.status_code");
//    }
//}
