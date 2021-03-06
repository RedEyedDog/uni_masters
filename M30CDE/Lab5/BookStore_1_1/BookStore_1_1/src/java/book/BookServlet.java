/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package book;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author nazaraf
 */
@WebServlet(name = "BookServlet", urlPatterns = {"/BookServlet"})
public class BookServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        System.out.println(" Request Processing ... ");

        HttpSession session = req.getSession(true);
        if (session == null) {
            res.sendRedirect("http://localhost:8080/error.html");
        }
        ArrayList<Book> list = (ArrayList<Book>) session.getAttribute("shoppingcart");
        String action = req.getParameter("action").trim();

        if (action.equals("ADD")) {
            if (list == null) {
                list = new ArrayList();
            }

            String bookReq = req.getParameter("book");

            if (Book.AVAILABLEBOOKS.containsKey(bookReq)) {
                Book availBook = Book.AVAILABLEBOOKS.get(bookReq);
                boolean found = false;
                for (int i = 0; i < list.size(); i++) {
                    Book item = list.get(i);

                    if (item.getName() == availBook.getName() && item.getAuthor() == availBook.getAuthor()) {
                        found = true;
                        int quantity = item.getQuantity() + 1;
                        item.setQuantity(quantity);
                    }
                }
                if (!found) {
                    Book book = new Book(availBook.getName(), availBook.getAuthor(), availBook.getPrice());
                    book.setQuantity(1);
                    list.add(book);
                }
                session.setAttribute("shoppingcart", list);
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
                dispatcher.forward(req, res);
            } else {
                res.sendRedirect("http://localhost:8080/error.html");
            }

        } //if add
        else if (action.equals("DELETE")) {

            int deleteindex = Integer.parseInt(req.getParameter("deleteindex"));

            if (deleteindex < list.size()) {
                list.remove(deleteindex);
            }

            session.setAttribute("shoppingcart", list);

            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, res);
        } else if (action.equals("CHECKOUT")) {

            float total = 0;
            for (int i = 0; i < list.size(); i++) {
                 total += list.get(i).getPrice();
            }

            req.setAttribute("amount", String.valueOf(total));
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/Checkout.jsp");
            dispatcher.forward(req, res);
        }
        

    }//end method
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private Book getBook(HttpServletRequest req) {

        String myBook = req.getParameter("book");
        String qty = req.getParameter("qty");
        StringTokenizer t = new StringTokenizer(myBook, "|");
        String name = t.nextToken();
        String author = t.nextToken();

        String price = t.nextToken();

        Book Book = new Book(name, author, Float.parseFloat(price));
        Book.setQuantity((new Integer(qty)).intValue());
        return Book;
    }
}
