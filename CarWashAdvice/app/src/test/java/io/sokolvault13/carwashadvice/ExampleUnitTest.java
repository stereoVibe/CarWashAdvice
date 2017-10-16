//package io.sokolvault13.carwashadvice;
//
//import android.util.JsonReader;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.io.StringReader;
//import java.nio.Buffer;
//
//import okhttp3.HttpUrl;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//import okhttp3.mockwebserver.RecordedRequest;
//
//import static org.junit.Assert.*;
//
///**
// * Example local unit test, which will execute on the development machine (host).
// *
// * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
// */
//public class ExampleUnitTest {
//    @Test
//    public void addition_isCorrect() throws Exception {
//        assertEquals(4, 2 + 2);
//    }
//
//    @Test
//    public void isJSONResponseReceived() throws IOException {
//        MockWebServer server = new MockWebServer();
////        server.enqueue(new MockResponse().setBody("Berlin"));
//        server.start();
//
//        HttpUrl baseUrl = server.url("http://api.apixu.com/v1/current.json?key=42dac746012a433cb34182822172908&q=Berlin");
//        String bodyOfRequest = sendGetRequest(new OkHttpClient(), baseUrl);
//        boolean isExist = bodyOfRequest.contains("Berlin");
//        Assert.assertTrue(isExist);
////        Assert.assertEquals("Berlin", bodyOfRequest);
//    }
//
//    private String sendGetRequest(OkHttpClient okHttpClient, HttpUrl baseUrl) throws IOException {
//        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"),"what?");
//
//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .post(requestBody)
//                .url(baseUrl)
//                .build();
//        Response response = okHttpClient.newCall(request).execute();
//
//        return response.body().string();
//    }
//
////    @Test
////    public void test() throws Exception {
////        // Create a MockWebServer. These are lean enough that you can create a new
////        // instance for every unit test.
////        MockWebServer server = new MockWebServer();
////
////        // Schedule some responses.
////        server.enqueue(new MockResponse().setBody("hello, world!"));
////        server.enqueue(new MockResponse().setBody("2"));
////
////        // Start the server.
////        server.start();
////
////        // Ask the server for its URL. You'll need this to make HTTP requests.
////        HttpUrl baseUrl = server.url("");
////
////
////        // Exercise your application code, which should make those HTTP requests.
////        // Responses are returned in the same order that they are enqueued.
////
////        // Optional: confirm that your app made the HTTP requests you were expecting.
////        RecordedRequest request1 = server.takeRequest();
////        assertEquals("/v1/chat/getBodyResponse/", request1.getPath());
////        assertNotNull(request1.getHeader("Authorization"));
////
//////        RecordedRequest request2 = server.takeRequest();
//////        assertEquals("/v1/chat/messages/2", request2.getPath());
//////
//////        RecordedRequest request3 = server.takeRequest();
//////        assertEquals("/v1/chat/messages/3", request3.getPath());
////
////        // Shut down the server. Instances cannot be reused.
////        server.shutdown();
////    }
//}