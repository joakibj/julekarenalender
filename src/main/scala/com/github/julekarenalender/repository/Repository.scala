package com.github.julekarenalender.repository

trait Repository {
  type T

  def find(id: Int): Option[T]
  def findAll(): List[T]
  def insert(t: T): Int
  def insertAll(lt: List[T])
  def delete(id: Int)
  def deleteAll()
}
