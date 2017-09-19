package g3tenantperschematest

import static grails.gorm.multitenancy.Tenants.*;
import g3tenantperschematest.Book;

class BootStrap {

    def init = { servletContext ->

      List<Book> books_test1 = withId("test1") {
          Book.list()
      }

      List<Book> books_test2 = withId("test2") {
          Book.list()
      }
    }

    def destroy = {
    }
}
