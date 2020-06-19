package cars.details.tests;

import cartax.page.objects.ConfirmDetailsPage;
import framework.EventBrowser;
import framework.FilesOperations;
import framework.SeleniumCommands;
import org.testng.Assert;
import org.testng.annotations.Guice;
import com.google.inject.Inject;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@Guice
public class CarsTestsImplementation {

	@Inject EventBrowser eb;
	@Inject SeleniumCommands sc;
	@Inject ConfirmDetailsPage cdp;
	@Inject	FilesOperations fo;

	private static List<HashMap<String, String>> expectedCarDetails = new ArrayList<>();
	private static List<HashMap<String, String>> carRegNumber = new ArrayList<>();
	private static List<String> errors = new ArrayList<>();
	private static Scenario scenario;
	private static enum carDetail{REGISTRATION, COLOR, MODEL, MAKE};

	@Before
	public void beforeScenario(Scenario scenario) {
		this.scenario = scenario;
	}


	@Given("^I read registration details from ([^\"]*) and verify match against ([^\"]*)$")
	public void readAllFilesFromPath(String regNumbers, String expectedDetails) {
		Pattern regex = Pattern.compile("(\\b[A-Z][A-Z]\\d\\d [A-Z][A-Z][A-Z]|\\b[A-Z][A-Z]\\d\\d[A-Z][A-Z][A-Z]\\b)");
		carRegNumber = fo.extractRegistration(fo.readFile(regNumbers).toString(), regex);
		expectedCarDetails = fo.getCarDetails(fo.readFile(expectedDetails));
		Assert.assertTrue(expectedCarDetails.size() > 0, "No car details have been found!");
		Assert.assertTrue(expectedCarDetails.size() == carRegNumber.size(), "Actual and expected files do NOT match");
	}


	@When("^I compare the details on cartaxcheck website$")
	public void compareDVLAWebsiteByRegistrationNumber() {
		for(int i = 0; i < expectedCarDetails.size(); i++) {
			String carColour = expectedCarDetails.get(i).get(carDetail.COLOR.toString());
			String expectedRegNumber = expectedCarDetails.get(i).get(carDetail.REGISTRATION.toString());
			String carMake = expectedCarDetails.get(i).get(carDetail.MAKE.toString());
			String carModel = expectedCarDetails.get(i).get(carDetail.MODEL.toString());
			String actualRegNumber = carRegNumber.get(i).get(carDetail.REGISTRATION.toString());
			cdp.setUrl(actualRegNumber);
			cdp.get();
			scenario.write("Verify car with registration number: " + actualRegNumber);
			if (!cdp.getRegistrationNumber().equalsIgnoreCase(expectedRegNumber)) {
				String registrationNumberError = "Registration Number does not match, file value: " + expectedRegNumber + " and Cartax: " + cdp.getRegistrationNumber();
				errors.add(registrationNumberError);
				scenario.write(expectedRegNumber);
			}
			if (!cdp.getColour().equalsIgnoreCase(carColour)) {
				String colourError = "Colour does not match, file value: " + carColour + " and Cartax: " + cdp.getColour();
				errors.add(expectedRegNumber + ": " + colourError);
				scenario.write(colourError);
			}

			if (!cdp.getMake().equalsIgnoreCase(carMake)) {
				String makeError = "Make Number does not match, file value: " + carMake + " and Cartax: " + cdp.getMake();
				errors.add(expectedRegNumber + ": " + makeError);
				scenario.write(makeError);
			}

			if (!cdp.getModel().equalsIgnoreCase(carModel)) {
				String modelError = "Make Number does not match, file value: " + carModel + " and Cartax: " + cdp.getModel();
				errors.add(expectedRegNumber + ": " + modelError);
				scenario.write(modelError);
			}
			scenario.embed(sc.getScreenShot(), "image/png");
		}
	}


	@Then("^The details should match$")
	public void verifyDetailsMatch() {
		Assert.assertTrue(errors.size() == 0,
				"Unmatched data found, verify the generated report!");
	}


	@After
	public void finish(Scenario scenario) {
		for(int i = 0; i < errors.size(); i++) {
			scenario.write(errors.get(i));
		}
	}
}
