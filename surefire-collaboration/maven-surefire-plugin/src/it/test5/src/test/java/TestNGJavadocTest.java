import org.testng.Assert;


/**
 * Simple javadoc based 1.4 jre unit test.
 */
public class TestNGJavadocTest {

	/**
	 * Sets up testObject
	 * @testng.configuration beforeTestClass = "true"
	 * 						 groups = "functional"
	 */
	public void configureTest()
	{
		testObject = new Object();
	}
	
	Object testObject;
	
	/**
	 * Tests reporting an error
	 * @testng.test groups = "functional, notincluded"
	 */
	public void isTestObjectNull()
	{
		Assert.assertNotNull(testObject, "testObject is null");
	}
	
	/**
	 * Sample method that shouldn't be run by test suite.
	 * @testng.test groups = "notincluded"
	 */
	public void shouldNotRun()
	{
		Assert.assertTrue(false, "Group specified by test shouldnt be run.");
	}
}