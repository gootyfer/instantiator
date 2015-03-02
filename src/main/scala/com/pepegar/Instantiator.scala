package com.pepegar

import com.pepegar.instantiator.tree._
import com.pepegar.instantiator.types
import scala.reflect.runtime.universe.{typeOf, Type, ClassSymbol, TypeTag}
import util.Random

/** Instantiator object is the main object and entry point to the library.
 *
 * it exposes a single function, ```createInstance``` which is a generic method. That createInstance
 * method takes a type as parameter and returns a valid instance of it.
 *
 * @author pepegar
 */
object Instantiator {

  /** Nothing to see here yet
   *
   * @author pepegar
   */
  def createInstance[T](implicit tag: TypeTag[T]) = {
    val t = typeOf[T]
    generateTypesTree(t)
  }

  /** This method generates the type tree of properties of the given type
   *
   * The purpose of it is creating a Tree that will be parsed later in the moment of generating the
   * instance of the class.
   *
   * @author pepegar
   * */
  def generateTypesTree(tpe: Type): Tree[ClassSymbol] = {
    val symbol = tpe.typeSymbol
    val classProperties = tpe.members.filter(!_.isMethod)

    classProperties.isEmpty match {
      case true => Leaf(symbol.asClass)
      case false => Branch(classProperties.map(s => generateTypesTree(s.typeSignature)).toList)
    }
  }

  def mapToValuesTree(typesTree: Tree[ClassSymbol]): Tree[Any] = {
    typesTree.scan(symbolToValue)
  }

  def symbolToValue(s: ClassSymbol): Any = {
    s match {
      case s if s.toString == types.INT => Random.nextInt
      case s if s.toString == types.STRING => Random.alphanumeric.take(10).toList.mkString("")
      case s if s.toString == types.FLOAT => Random.nextFloat
      case s if s.toString == types.BOOLEAN => Random.nextBoolean
      case s if s.toString == types.BYTE => Random.nextInt.toByte
      case s if s.toString == types.SHORT => Random.nextInt(Short.MaxValue).toShort
      case s if s.toString == types.CHAR => Random.alphanumeric.take(1)(0)
      case s if s.toString == types.LONG => Random.nextLong
      case s if s.toString == types.DOUBLE => Random.nextDouble
      case _ =>
    }
  }
}
