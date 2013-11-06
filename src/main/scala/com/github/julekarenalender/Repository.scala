package com.github.julekarenalender

trait Repository {
  type T

  def find(id: Int): Option[T]
  def findAll(): List[T]
  def insert(t: T): Unit
  def insertAll(lt: List[T]): Unit
}
