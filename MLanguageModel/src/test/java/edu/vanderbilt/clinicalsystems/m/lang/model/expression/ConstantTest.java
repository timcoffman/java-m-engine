package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ConstantTest {

	@Test
	public void canRepresentNull() {
		Constant constant = Constant.from(null);
		assertThat( constant.representsNull(), equalTo(true) ) ;
		assertThat( constant.representsNumber(   0.123 ), equalTo(false) ) ;
		assertThat( constant.representsNumber(  12.3   ), equalTo(false) ) ;
		assertThat( constant.representsNumber( 123     ), equalTo(false) ) ;
		assertThat( constant, not(equalTo("abc")) ) ;
		assertThat( constant, not(equalTo("123")) ) ;
		assertThat( constant, equalTo( new Constant() ) ) ;
		assertThat( constant, equalTo( new Constant( "" ) ) ) ;
		assertThat( constant, equalTo( new Constant( ConstantSupport.NULL_VALUE ) ) ) ;
		assertThat( constant, equalTo( ConstantSupport.NULL_VALUE ) ) ;
	}

	@Test
	public void canRepresentPlainString() {
		Constant constant = Constant.from("abc");
		assertThat( constant.representsNull(), equalTo(false) ) ;
		assertThat( constant.representsNumber(   0.123 ), equalTo(false) ) ;
		assertThat( constant.representsNumber(  12.3   ), equalTo(false) ) ;
		assertThat( constant.representsNumber( 123     ), equalTo(false) ) ;
		assertThat( constant,     equalTo("abc")  ) ;
		assertThat( constant, not(equalTo("123")) ) ;
	}

	@Test
	public void canRepresentZeroString() {
		Constant constant = Constant.from("0");
		assertThat( constant.representsNull(), equalTo(false) ) ;
		assertThat( constant.representsNumber(   0     ), equalTo(true) ) ;
		assertThat( constant.representsNumber(   0.123 ), equalTo(false) ) ;
		assertThat( constant.representsNumber(  12.3   ), equalTo(false) ) ;
		assertThat( constant.representsNumber( 123     ), equalTo(false) ) ;
		assertThat( constant,     equalTo(  "0")  ) ;
		assertThat( constant, not(equalTo( "00")) ) ;
		assertThat( constant, not(equalTo("abc")) ) ;
		assertThat( constant, not(equalTo("123")) ) ;
	}

	@Test
	public void canRepresentIntegerString() {
		Constant constant = Constant.from("123");
		assertThat( constant.representsNull(), equalTo(false) ) ;
		assertThat( constant.representsNumber(   0     ), equalTo(false) ) ;
		assertThat( constant.representsNumber(   0.123 ), equalTo(false) ) ;
		assertThat( constant.representsNumber(  12.3   ), equalTo(false) ) ;
		assertThat( constant.representsNumber( 123     ), equalTo(true) ) ;
		assertThat( constant, not(equalTo(  "0")) ) ;
		assertThat( constant, not(equalTo("abc")) ) ;
		assertThat( constant,     equalTo("123")  ) ;
	}

	@Test
	public void canRepresentSingleDigitIntegerString() {
		Constant constant = Constant.from("7");
		assertThat( constant.representsNull(), equalTo(false) ) ;
		assertThat( constant.representsNumber(   0     ), equalTo(false) ) ;
		assertThat( constant.representsNumber(   7     ), equalTo(true) ) ;
		assertThat( constant.representsNumber(   0.123 ), equalTo(false) ) ;
		assertThat( constant.representsNumber(  12.3   ), equalTo(false) ) ;
		assertThat( constant.representsNumber( 123     ), equalTo(false) ) ;
		assertThat( constant, not(equalTo(  "0")) ) ;
		assertThat( constant, not(equalTo("abc")) ) ;
		assertThat( constant, not(equalTo("123")) ) ;
	}

	@Test
	public void canRepresentDecimalString() {
		Constant constant = Constant.from("12.3");
		assertThat( constant.representsNull(), equalTo(false) ) ;
		assertThat( constant.representsNumber(   0     ), equalTo(false) ) ;
		assertThat( constant.representsNumber(   0.123 ), equalTo(false) ) ;
		assertThat( constant.representsNumber(  12.3   ), equalTo(true) ) ;
		assertThat( constant.representsNumber( 123     ), equalTo(false) ) ;
		assertThat( constant, not(equalTo(  "0")) ) ;
		assertThat( constant, not(equalTo("abc")) ) ;
		assertThat( constant, not(equalTo("123")) ) ;
	}

	@Test
	public void canRepresentDecimalStringLessThanOne() {
		Constant constant = Constant.from("0.123");
		assertThat( constant.representsNull(), equalTo(false) ) ;
		assertThat( constant.representsNumber(   0     ), equalTo(false) ) ;
		assertThat( constant.representsNumber(   0.123 ), equalTo(true) ) ;
		assertThat( constant.representsNumber(  12.3   ), equalTo(false) ) ;
		assertThat( constant.representsNumber( 123     ), equalTo(false) ) ;
		assertThat( constant, not(equalTo(  "0")) ) ;
		assertThat( constant, not(equalTo("abc")) ) ;
		assertThat( constant, not(equalTo("123")) ) ;
	}

}
