package com.app.service;


import com.app.model.*;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

public interface BookService extends Remote {
    public User findUserByLogin(String login) throws RemoteException;
    public User saveUser(String login,String firstName, String lastName, String pass,String role) throws RemoteException;
    public User auth(String login,String pass) throws RemoteException;
    public Genre createGenre(String name, String login)  throws RemoteException;
    public Genre updateGenre(Genre genre,String login)  throws RemoteException;
    public boolean deleteGenre(Genre genre,String login)  throws RemoteException;
    public List<Genre> getAllGenre(String login) throws RemoteException;

    public Author createAuthor(String firstName, String lastName, String login)  throws RemoteException;
    public Author updateAuthor(Author author,String login)  throws RemoteException;
    public boolean deleteAuthor(Author author,String login)  throws RemoteException;
    public List<Author> getAllAuthor(String login) throws RemoteException;

    public Book createBook(String name, Genre genre, Author author, String login)  throws RemoteException;
    public Book updateBook(Book book,String login)  throws RemoteException;
    public boolean deleteBook(Book book,String login)  throws RemoteException;
    public List<Book> getAllBook(String login) throws RemoteException;
    public List<Book> getAllBookByAuthor(Author author,String login) throws RemoteException;

    public Arrival createArrival(Arrival arrival, String login)  throws RemoteException;
    public Arrival updateArrival(Arrival arrival,String login)  throws RemoteException;
    public boolean deleteArrival(Arrival arrival,String login)  throws RemoteException;
    public List<Arrival> getAllArrival(String login) throws RemoteException;
    public List<Arrival> getAllArrivalByPeriod(LocalDate begin, LocalDate end, String login) throws RemoteException;

    public List<GenreReport> getGenreCount(String login) throws RemoteException;
    public List<YearReport> getYearCount(String login)throws RemoteException;







}
