package password.manager;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

public class BrowserView {
	
	private String websiteAddress;
	
	public BrowserView() {
		websiteAddress = null;
	}
	
	public BrowserView(String url) {
		websiteAddress = url;
	}
	
	public void changeSite(String url) {
		websiteAddress = url;
	}
	
	public void gotoSite() {
		if (websiteAddress != null) {
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.browse(new URI(websiteAddress)); //Feeds browser address.
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
				} catch (URISyntaxException e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			} else {
				Runtime runtime = Runtime.getRuntime(); //If not on windows os, will try to execute command to runtime.
				try {
					runtime.exec("xdg-open " + websiteAddress); //Potential linux support untested.
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
