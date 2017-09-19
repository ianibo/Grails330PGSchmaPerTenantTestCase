package g3tenantperschematest

import grails.gorm.MultiTenant;

class Book implements MultiTenant<Book> {

  String title

  static constraints = {
  }
}
