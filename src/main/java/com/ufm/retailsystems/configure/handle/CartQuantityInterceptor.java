package com.ufm.retailsystems.configure.handle;


import com.ufm.retailsystems.dto.cart.CartItem;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Component
public class CartQuantityInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            HttpSession session = request.getSession();
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            int quantity = (cart != null) ? cart.size() : 0;
            modelAndView.addObject("quantity", quantity);
        }
        if (modelAndView != null) {
            Authentication userDetails = SecurityContextHolder.getContext().getAuthentication();
            String username = (!"anonymousUser".equals(userDetails.getName())) ? userDetails.getName() : "Đăng nhập";
            modelAndView.addObject("username", username);
        }

    }

}
