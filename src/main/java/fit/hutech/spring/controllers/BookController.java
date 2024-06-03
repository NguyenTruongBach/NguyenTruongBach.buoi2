package fit.hutech.spring.controllers;

import fit.hutech.spring.entities.Book;
import fit.hutech.spring.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping()
    public String showAllBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "book/list";
    }

    @GetMapping("/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "book/add";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute("book") Book book, Model model) {
        if (book.getTitle() == null || book.getTitle().isEmpty() ||
                book.getAuthor() == null || book.getAuthor().isEmpty() ||
                book.getPrice() == null || book.getCategory() == null || book.getCategory().isEmpty()) {
            model.addAttribute("error", "All fields are required!");
            return "book/add";
        }
        if (bookService.getBookById(book.getId()).isEmpty()) {
            bookService.addBook(book);
        }
        return "redirect:/books/list";
    }

    @GetMapping("/edit/{id}")
    public String editBookForm(Model model, @PathVariable long id) {
        var book = bookService.getBookById(id).orElse(null);
        model.addAttribute("book", book != null ? book : new Book());
        return "book/edit";
    }

    @PostMapping("/edit")
    public String editBook(@ModelAttribute("book") Book book, Model model) {
        if (book.getTitle() == null || book.getTitle().isEmpty() ||
                book.getAuthor() == null || book.getAuthor().isEmpty() ||
                book.getPrice() == null || book.getCategory() == null || book.getCategory().isEmpty()) {
            model.addAttribute("error", "All fields are required!");
            return "book/edit";
        }
        bookService.updateBook(book);
        return "redirect:/books/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable long id) {
        if (bookService.getBookById(id).isPresent()) {
            bookService.deleteBookById(id);
        }
        return "redirect:/books/list";
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam("keyword") String keyword, Model model) {
        List<Book> searchResult = bookService.searchBooks(keyword);
        model.addAttribute("books", searchResult);
        model.addAttribute("keyword", keyword);
        return "book/list";
    }

    @GetMapping("/autocomplete")
    @ResponseBody
    public List<Book> autocomplete(@RequestParam("keyword") String keyword) {
        return bookService.searchBooks(keyword);
    }

    @GetMapping("/findById")
    public String findBookById(@RequestParam("id") Long id, Model model) {
        var book = bookService.getBookById(id);
        model.addAttribute("books", book.isPresent() ? List.of(book.get()) : List.of());
        return "book/list";
    }
}
