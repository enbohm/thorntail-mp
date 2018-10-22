package se.enbohms.thorntail;

import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

@RunWith(Arquillian.class)
public class HelloEndpointTest {

	@ArquillianResource
	private URL baseURL;

	@Deployment
	public static JAXRSArchive createDeployment() throws Exception {
		JAXRSArchive archive = ShrinkWrap.create(JAXRSArchive.class, "thorntail-mp.war");
		archive.addPackage(HelloEndpoint.class.getPackage());
		archive.addAsResource("project-defaults.yml");
		archive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		archive.addAllDependencies();
		return archive;
	}

	@Test
	@RunAsClient
	public void should_return_valid_name() throws Exception {
		HttpURLConnection conn = getURLConnection("GET", "thorntail/hello/JD");
		JsonReader jsonReader = Json.createReader(conn.getInputStream());
		JsonObject jsonObject = jsonReader.readObject();
		assertEquals("Name should be JD", "JD", jsonObject.getString("Hello"));
	}

	private HttpURLConnection getURLConnection(String method, String path) throws Exception {
		URL url = new URL(baseURL + path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setRequestProperty("Accept", "application/json");
		return conn;
	}
}
