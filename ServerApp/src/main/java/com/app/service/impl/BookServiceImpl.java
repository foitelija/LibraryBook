package com.app.service.impl;

import com.app.dao.*;
import com.app.model.*;
import com.app.service.PasswordEncoder;
import com.app.service.BookService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * реализация BookService
 */
public class BookServiceImpl extends UnicastRemoteObject implements BookService {
    private static Logger LOG = Logger.getLogger(BookServiceImpl.class.getName());


    private final DAOFactory daoFactory;
    private final UserDao userDao;
    private final GenreDao genreDao;
    private final AuthorDao authorDao;
    private final BookDao bookDao;
    private final ArrivalDao arrivalDao;

    public BookServiceImpl() throws RemoteException, SQLException {
        this.daoFactory = new DAOFactory();
        this.userDao = daoFactory.getUserDao();
        this.genreDao = daoFactory.getGenreDao();
        this.authorDao = daoFactory.getAuthorDao();
        this.bookDao =daoFactory.getBookDao();
        this.arrivalDao = daoFactory.getArrivalDao();
    }

    @Override
    public User findUserByLogin(String login) throws RemoteException {
        LOG.info(String.format("find user by login '%s'",login));
        try {
            User user = userDao.get(login).orElse(null);
            if (user != null) return user;
        } catch (SQLException e) {
            throw new RemoteException("Ошибка! " + e.getMessage());
        }
        throw new RemoteException("Пользователь не найден!");
    }

    @Override
    public User saveUser(String login, String firstName, String lastName, String pass, String role) throws RemoteException {
        LOG.info(String.format("create user  %s %s, login %s, role  %s ",firstName,lastName,login,role));
        try {
            User user = userDao.get(login).orElse(null);
            if (user != null) {

                throw new RemoteException("Даный логин занят!");
            }
            user = new User();
            user.setLogin(login);
            user.setLastName(login);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(PasswordEncoder.encodePassword(pass));
            user.setRole(Role.valueOf(role));
            String newLogin = userDao.save(user);
            if (newLogin == null) throw new RemoteException("Ошибка записи");
            user = userDao.get(newLogin).get();
            return user;

        } catch (SQLException | RemoteException e) {
            LOG.warning("error create user "+e.getMessage());
            throw new RemoteException("Ошибка записи" + e.getMessage());
        }
    }

    @Override
    public User auth(String login, String pass) throws RemoteException {
        LOG.info("auth  user "+login);
        User user = null;
        try {
            user = userDao.get(login).orElse(null);
            if (user == null) throw new RemoteException("Пользователь не найден!");
            String hash = PasswordEncoder.encodePassword(pass);
            if (user.getPassword().equalsIgnoreCase(hash)) {
                LOG.info("succes auth  user  "+login);
                return user;
            }
        } catch (SQLException | RemoteException e) {
            LOG.info("error auth  user  "+login+" "+e.getMessage());
            throw new RemoteException("Ошибка базы" + e.getMessage());
        }
        LOG.info("wrong auth  user  "+login);
        throw new RemoteException("Не верный логин или пароль!");
    }

    @Override
    public Genre createGenre(String name, String login) throws RemoteException {
        LOG.info(String.format("create genre  %s  login %s ",name,login));
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            Genre genre =new Genre(name);
            genre.setModifiedBy((User) user.get());
            int id = genreDao.save(genre);
            LOG.info(String.format("genre  %s  id %d ",name,id));
            if (id==0) throw new RemoteException("Ошибка создания!");
            return  genreDao.get(id).get();
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("create genre  %s  error %s ",name,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public Genre updateGenre(Genre genre, String login) throws RemoteException {
        LOG.info(String.format("update genre login %s ",login));
        LOG.info(genre.toString());
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            genreDao.get(genre.getId()).orElseThrow(()->new RemoteException("Жанр не найден"));
            genre.setModifiedBy((User) user.get());
            genreDao.update((Genre) genre);

            return  genreDao.get(genre.getId()).get();
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("error update genre %d %s  login %s -- %s",genre.getId(),genre.getName(),login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public boolean deleteGenre(Genre genre, String login) throws RemoteException {
        LOG.info(String.format("delete genre %d  login %s ",genre.getId(),login));
        LOG.info(genre.toString());
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            genreDao.get(genre.getId()).orElseThrow(()->new RemoteException("Жанр не найден"));

            if (user.get().getRole()==Role.MANAGER){
                genre.setModifiedBy((User) user.get());
                genre.setVisible(false);
                genreDao.update((Genre) genre);
                return !genreDao.get(genre.getId()).get().isVisible();
            }else {
                genreDao.delete(genre.getId());
                return !genreDao.get(genre.getId()).isPresent();
            }
        } catch (SQLException | RemoteException e) {
            e.printStackTrace();
            LOG.warning(String.format("delete genre %d  login %s  error -- ",genre.getId(),login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public List<Genre> getAllGenre(String login) throws RemoteException {
        LOG.info(String.format("select all  genre  login %s ",login));
        Optional<User> user = null;
        try {
            user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            List<Genre> genres = genreDao.getAll();
            if (user.get().getRole()==Role.MANAGER)
                genres.removeIf(g->g.isVisible()==false);
            return genres;
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("select all  genre   login %s  error -- ",login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }

    }

    @Override
    public Author createAuthor(String firstName, String lastName, String login) throws RemoteException {
        LOG.info(String.format("create author  %s  %s login %s ",firstName,lastName,login));
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            Author author =new Author(firstName,lastName);
            author.setModifiedBy((User) user.get());
            int id = authorDao.save(author);
            if (id==0) throw new RemoteException("Ошибка создания");
            return  authorDao.get(id).get();
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("create author  login %s  error -- ",login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public Author updateAuthor(Author author, String login) throws RemoteException {
        LOG.info(String.format("update author  login %s ",login));
        LOG.info(author.toString());
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            authorDao.get(author.getId()).orElseThrow(()->new RemoteException("Автор  не найден"));
            author.setModifiedBy((User) user.get());
            authorDao.update((Author) author);
            return  authorDao.get(author.getId()).get();
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("update author %d  login %s  error -- ",author.getId(),login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public boolean deleteAuthor(Author author, String login) throws RemoteException {
        LOG.info(String.format("delete author  login %s ",login));
        LOG.info(author.toString());
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            authorDao.get(author.getId()).orElseThrow(()->new RemoteException("Автор не найден"));

            if (user.get().getRole()==Role.MANAGER){
                author.setModifiedBy((User) user.get());
                author.setVisible(false);
                authorDao.update((Author) author);
                return !authorDao.get(author.getId()).get().isVisible();
            }else {
                authorDao.delete(author.getId());
                return !authorDao.get(author.getId()).isPresent();
            }
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("delete author %d  login %s  error -- ",author.getId(),login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public List<Author> getAllAuthor(String login) throws RemoteException {
        LOG.info(String.format("get all author  login %s ",login));
        Optional<User> user = null;
        try {
            user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            List<Author> authors = authorDao.getAll();
            if (user.get().getRole()==Role.MANAGER)
                authors.removeIf(g->g.isVisible()==false);
            return authors;
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("get all author   login %s  error -- ",login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public Book createBook(String name,  Genre genre, Author author, String login) throws RemoteException {
        LOG.info(String.format("create book %s    login %s ",name,login));
        LOG.info(genre.toString());
        LOG.info(author.toString());
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            authorDao.get(author.getId()).orElseThrow(()->new RemoteException("Автор не найден"));
            genreDao.get(genre.getId()).orElseThrow(()->new RemoteException("Жанр не найден"));
                Book book =new Book();
                book.setName(name);
                book.setAuthor(author);
                book.setGenre(genre);
                book.setModifiedBy(user.get());
            int id = bookDao.save(book);
            return  bookDao.get(id).get();
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("create book   login %s  error -- ",login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public Book updateBook(Book book, String login) throws RemoteException {
        LOG.info(String.format("update book  login %s ", login));
        LOG.info(book.toString());
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            authorDao.get(book.getAuthor().getId()).orElseThrow(()->new RemoteException("Автор не найден"));
            genreDao.get(book.getGenre().getId()).orElseThrow(()->new RemoteException("Жанр не найден"));
            book.setModifiedBy(user.get());
            bookDao.update((Book) book);
            return  bookDao.get(book.getId()).get();
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("update book   login %s  error -- ",login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public boolean deleteBook(Book book, String login) throws RemoteException {
        LOG.info(String.format("delete booklogin %s ",login));
        LOG.info(book.toString());
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            bookDao.get(book.getId()).orElseThrow(()->new RemoteException("Книга не найдена"));

            if (user.get().getRole()==Role.MANAGER){
                book.setModifiedBy(user.get());
                book.setVisible(false);
                bookDao.update((Book) book);
                return !bookDao.get(book.getId()).get().isVisible();
            }else {
                bookDao.delete(book.getId());
                return !bookDao.get(book.getId()).isPresent();
            }
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("delete book   login %s  error -- ",login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public List<Book> getAllBook(String login) throws RemoteException {
        LOG.info(String.format("get all  book  login %s ",login));
        Optional<User> user = null;
        try {
            user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            List<Book> books = bookDao.getAll();
            if (user.get().getRole()==Role.MANAGER)
                books.removeIf(g->g.isVisible()==false);
            return books;
        } catch (SQLException | RemoteException e) {
            e.printStackTrace();
            LOG.warning(String.format("get all book   login %s  error -- ",login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public List<Book> getAllBookByAuthor(Author author,String login) throws RemoteException {
        LOG.info(String.format("get all  book by author  login %s ", login));
        LOG.info(author.toString());

        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            authorDao.get(author.getId()).orElseThrow(()->new RemoteException("Автор не найден"));
            List<Book> books = bookDao.getByAuthor((Author) author);
            if (user.get().getRole()==Role.MANAGER)
                books.removeIf(g->g.isVisible()==false);
            return books;
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("get all book by author  login %s  error -- ",login,e.getMessage()));
            throw new RemoteException(e.getCause().getMessage());
        }
    }

    @Override
    public Arrival createArrival(Arrival arrival, String login) throws RemoteException {
        LOG.info(String.format("create arrival   login %s ",login));
        LOG.info(arrival.toString());
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            bookDao.get(arrival.getBook().getId()).orElseThrow(()->new RemoteException("Книга не найдена"));
            arrival.setModifiedBy(user.get());
            int id = arrivalDao.save((Arrival) arrival);
            return  arrivalDao.get(id).get();
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("create arrival   login %s  error -- ",login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public Arrival updateArrival(Arrival arrival, String login) throws RemoteException {
        LOG.info(String.format("update arrival   login %s ",login));
        LOG.info(arrival.toString());
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            bookDao.get(arrival.getBook().getId()).orElseThrow(()->new RemoteException("Книга не найдена"));
            arrival.setModifiedBy(user.get());
            arrivalDao.update( arrival);
            return arrivalDao.get(arrival.getId()).get();
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("update arrival   login %s  error -- ",login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public boolean deleteArrival(Arrival arrival, String login) throws RemoteException {
        LOG.info(String.format("delete arrival   login %s ",login));
        LOG.info(arrival.toString());
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            bookDao.get(arrival.getBook().getId()).orElseThrow(()->new RemoteException("Книга не найдена"));

            if (user.get().getRole()==Role.MANAGER){
                arrival.setModifiedBy(user.get());
                arrival.setVisible(false);
                arrivalDao.update( arrival);
                return !arrivalDao.get(arrival.getId()).get().isVisible();
            }else {
                arrivalDao.delete(arrival.getId());
                return !arrivalDao.get(arrival.getId()).isPresent();
            }
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("delete arrival  login %s  error -- ",login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }

    }

    @Override
    public List<Arrival> getAllArrival(String login) throws RemoteException {
        LOG.info(String.format("get all arrival   login %s ",login));
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            List<Arrival> arrivals = arrivalDao.getAll();
            if (user.get().getRole()==Role.MANAGER) arrivals.removeIf(a->!a.isVisible());
            return arrivals;
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("get all arrival  login %s  error -- ",login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public List<Arrival> getAllArrivalByPeriod(LocalDate begin, LocalDate end, String login) throws RemoteException {
        LOG.info(String.format("get all arrival  by period  login %s ",login));
        LOG.info("from "+begin.toString()+" to "+end.toString());
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            List<Arrival> arrivals = arrivalDao.getByPeriod(begin,end);
            if (user.get().getRole()==Role.MANAGER) arrivals.removeIf(a->!a.isVisible());
            return arrivals;
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("get all arrival by period login %s  error -- ",login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public List<GenreReport> getGenreCount(String login) throws RemoteException {
        LOG.info(String.format("get report genre count login %s ",login));
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            return bookDao.genreCount().stream().limit(5).collect(Collectors.toList());
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("get report genre count login %s  error -- ",login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public List<YearReport> getYearCount(String login) throws RemoteException {
        LOG.info(String.format("get report year count login %s ",login));
        try {
            Optional<User> user = userDao.get(login);
            if (!user.isPresent()) throw new RemoteException("Пользователь не найден");
            return arrivalDao.yearBookCount().stream().limit(10).sorted((r1,r2)->Integer.compare(r1.getYearOf(),r2.getYearOf())).collect(Collectors.toList());
        } catch (SQLException | RemoteException e) {
            LOG.warning(String.format("get report year count  login %s  error -- ",login,e.getMessage()));
            throw new RemoteException(e.getMessage());
        }
    }
}
