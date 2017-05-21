package updater;

public class SoftwareNotFoundException  extends Exception {

	public SoftwareNotFoundException(String productName) {
		super("Unable to find version for " + productName);
	}
}
