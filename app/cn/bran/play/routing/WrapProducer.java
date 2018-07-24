/**
 * 
 */
package cn.bran.play.routing;

import play.mvc.Result;

/**
 * @author bran
 * 
 */
public class WrapProducer extends Result {
	private String produces;
	private Result r;

	public WrapProducer(String _produces, Result _r) {
		super(_r.status(),_r.body());
		this.produces = _produces;
		this.r = _r;
	}

//	public play.api.mvc.Result getWrappedResult() {
//		return r.getWrappedResult().as(produces);
//	}

	/* (non-Javadoc)
	 * @see play.mvc.SimpleResult#getWrappedSimpleResult()
	 */

	@Override
	public play.api.mvc.Result asScala() {
		return this.r.asScala().as(this.produces);
	}
}