/**
 *
 */
package cn.bran.play.routing

import java.lang.reflect.Method
import play.core.j.{ JavaAction, JavaActionAnnotations}
import play.mvc.{ Action => JAction, Result => JResult }
import play.libs.F.{ Promise => JPromise }
import play.core.j.JavaActionAnnotations
import play.core.j.JavaHandlerComponents
/*
class JavaActionBridge(components: JavaHandlerComponents) extends play.core.j.JavaAction with play.api.mvc.Handler {
  def this(targetClass: Class[_], meth: Method, resultBuilder: ResultBuilder) {
    this(null);
    var c = play.Play.application().injector.instanceOf([JavaHandlerComponents]);
  }
  val annotations = new JavaActionAnnotations(targetClass, meth)
  val parser = annotations.parser
  def invocation: JPromise[JResult] = {
    JPromise.pure(resultBuilder.create())
  }
}
*/
