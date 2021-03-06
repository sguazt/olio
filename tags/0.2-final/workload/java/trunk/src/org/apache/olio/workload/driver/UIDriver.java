/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.olio.workload.driver;

import com.sun.faban.driver.*;
import com.sun.faban.common.Utilities;
import com.sun.faban.common.NameValuePair;
import com.sun.faban.driver.transport.hc3.ApacheHC3Transport;
import org.apache.olio.workload.util.RandomUtil;
import org.apache.olio.workload.util.ScaleFactors;
import org.apache.olio.workload.util.UserName;
import java.util.logging.Level;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

@BenchmarkDefinition (
    name    = "Olio Java Workload",
    version = "0.2",
    scaleName = "Concurrent Users"

)
@BenchmarkDriver (
    name           = "UIDriver",
    threadPerScale    = 1
)
        // 90/10 Read/Write ratio
       
@MatrixMix (
operations = { "HomePage", "Login", "TagSearch", "EventDetail", "PersonDetail",
    "AddPerson", "AddEvent" },

    mix = { @Row({  0, 11, 52, 36,  0, 1,  0 }),
            @Row({  0,  0, 60, 20,  0, 0, 20 }),
            @Row({ 21,  6, 41, 31,  0, 1,  0 }),
            @Row({ 72, 21,  0,  0,  6, 1,  0 }),
            @Row({ 52,  6,  0, 31, 11, 0,  0 }),
            @Row({ 0, 0,  0,  0,  100, 0,  0 }),
            @Row({  0,  0, 0, 100,  0, 0,  0 })
          }
)
@NegativeExponential (
    cycleType = CycleType.CYCLETIME,
    cycleMean = 5000,
    cycleDeviation = 2
)

public class UIDriver {
    // These are the common static files present in all pages.
    public static final String[] SITE_STATICS = {       
        "/css/scaffold.css",
        "/css/site.css",
        "/images/bg_main.png",
        "/images/RSS-icon-large.gif",
        "/images/php_bg_header.gif",
        "/images/php_main_nav_link_bg.gif",
        "/images/php_corner_top_right.gif",
        "/images/php_corner_top_left.gif",
        "/images/php_corner_bottom_right.gif",
        "/images/php_corner_bottom_left.gif",
        "/resources/jmaki-min.js",
        "/glue.js",
        "/resources/system-glue.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/yahoo-dom-event/yahoo-dom-event.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/element/element-beta-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/container/container_core-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/menu/menu-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/button/button-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/datasource/datasource-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/calendar/calendar-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/menu/assets/skins/sam/menu.css",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/button/assets/skins/sam/button.css",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/calendar/assets/skins/sam/calendar.css",
        "/resources/yahoo/calendar/component.js",
        "/images/php_reflec_tile.gif",
        "/images/php_reflec_left.gif",
        "/images/php_reflec_right.gif",
        "/resources/config.json",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/assets/skins/sam/sprite.png"        
    };

    // This is in adition to SITE_STATICS
    public static final String[] HOME_STATICS = {
        "/js/httpobject.js",
        "/js/dragdrop.js",
        "/js/effects.js",
        "/js/prototype.js"
    };
    
    // This is in addition to SITE_STATICS.
    public static final String[] EVENTDETAIL_COMMON_STATICS = { 
        "/js/starrating.js",
        "/js/httpobject.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/yahoo-dom-event/yahoo-dom-event.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/dragdrop/dragdrop-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/animation/animation-min.js",
        "/resources/yahoo/resources/libs/yahoo/v2.6.0/connection/connection-min.js",
        "/resources/yahoo/map/component.js",
        "/images/star_off.png",
        "/images/star_on.png"
    };
    
    public static final String[] EVENTDETAIL_BASE_STATICS = {
        "/js/attendee.js",
        "/js/comments.js"
    };
    
    public static final String[] EVENTDETAIL_JMAKI_STATICS = {
        "/resources/blueprints/list/attendeeList/component.css", 
        "/resources/blueprints/list/attendeeList/component.js",
        "/resources/blueprints/list/commentList/component.css",
        "/resources/blueprints/list/commentList/component.js"
    };
    
    public static final String[] ADDPERSON_STATICS = {
        "/js/validateform.js",
        "/js/httpobject.js"
    };

    // In addition to SITE_STATIC
    public static final String[] ADDEVENT_STATICS = {
        "/js/validateform.js",
        "/js/httpobject.js"
    };


    public static final String[] PERSON_STATICS = {
        "/js/httpobject.js"
    };
    
    public static final String[] PERSON_GETS = {
        "/api/person?user_name=@USER_NAME@_&actionType=get_friends",
        "/api/person?user_name=@USER_NAME@&actionType=get_attend_events",
        "/api/person?user_name=@USER_NAME@&actionType=get_posted_events"
    };

    // In addition to SITE_STATICS
    public static final String[] TAGSEARCH_STATICS = { 
        "/js/httpobject.js"
    };

    // We just need today's date. java.sql.date does not have any time anyway.
    public static final java.sql.Date BASE_DATE =
                                new java.sql.Date(System.currentTimeMillis());

    private DriverContext ctx;
    private HttpTransport http;
    private String baseURL, hostURL;
    private String personDetailURL;
    private String homepageURL, logoutURL, loginURL;
    private String tagSearchURL;
    private String addEventURL, addPersonURL, eventDetailURL;
    private String fileUploadPersonURL, fileUploadEventURL;
    private String addAttendeeURL, fileServiceURL; //GET update.php?id=$eventid
    private String[] homepageStatics, personStatics,
            tagSearchStatics, eventDetailStatics, addPersonStatics, addEventStatics;
    File eventImg, eventThumb, eventPdf, personImg, personThumb;
    private boolean isLoggedOn = false;
    private int loginTime;
    private String username;
    private boolean jMakiComponentsUsed = true;
    private boolean requestSiteStatics = true;
    Logger logger;
    private com.sun.faban.driver.util.Random random;
    private DateFormat df;
    private String selectedEvent;
    private int personsAdded = 0;
    private int loadedUsers;
    private boolean isCached;
    private HashSet<String> cachedURLs = new HashSet<String>();
    private LinkedHashMap<String, String> loginHeaders =
            new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> cachedHeaders;
    private UIDriverMetrics driverMetrics;
    private long imgBytes = 0;
    private int imagesLoaded = 0;
    private String tagCloudURL;
    private StringBuilder tags = new StringBuilder();
    private LinkedHashSet<Integer> tagSet = new LinkedHashSet<Integer>(7);
    
    private boolean firstTime = true; // Work around for EclipseLink issue (under investigation)

    public UIDriver() throws XPathExpressionException {
        ctx = DriverContext.getContext();
        int scale = ctx.getScale();
        ScaleFactors.setActiveUsers(scale);
        HttpTransport.setProvider("com.sun.faban.driver.transport.hc3.ApacheHC3Transport");
        http = HttpTransport.newInstance();
        logger = ctx.getLogger();
        random = ctx.getRandom();
        driverMetrics = new UIDriverMetrics();
        ctx.attachMetrics(driverMetrics);        
        String hostPorts = ctx.getXPathValue(
                                "/olio/webServer/fa:hostConfig/fa:hostPorts");
        List<NameValuePair<Integer>> hostPortList =
                                            Utilities.parseHostPorts(hostPorts);
        /*
        String host = ctx.getXPathValue(
                                    "/olio/webServer/fa:hostConfig/fa:host");
        String port = ctx.getXPathValue("/olio/webServer/port");
        */
        int loadedScale = Integer.parseInt(
                                    ctx.getXPathValue("/olio/dbServer/scale"));
        loadedUsers = ScaleFactors.USERS_RATIO * loadedScale;
        if (scale > loadedScale)
            throw new FatalException("Data loaded only for " + loadedScale +
                    " concurrent users. Run is set for " + scale +
                    " concurrent users. Please load for enough concurrent " +
                    "users. Run terminating!");

        String type = ctx.getProperty("serverType");
        String resourcePath = ctx.getResourceDir();
        if (!resourcePath.endsWith(File.separator))
            resourcePath += File.separator;
        eventImg = new File(resourcePath + "event.jpg");
        // logger.info("eventImg: " + eventImg);
        eventThumb = new File(resourcePath + "event_thumb.jpg");
        // logger.info("eventThumb: " + eventThumb);
        eventPdf = new File(resourcePath + "event.pdf");
        // logger.info("eventPdf: " + eventPdf);
        personImg = new File(resourcePath + "person.jpg");
        // logger.info("personImg: " + personImg);
        personThumb = new File(resourcePath + "person_thumb.jpg");
        // logger.info("personThumb: " + personThumb);

        // Check whether jMaki component usage is disabled. default: true
        jMakiComponentsUsed = Boolean.parseBoolean(ctx.getProperty("useJMakiComponents"));
        // Check whether site statics should be requested for each page. Default: true.
        requestSiteStatics = !Boolean.parseBoolean(ctx.getProperty("noSiteStaticRequests"));
        // TEMP work around for a division by zero error
        try {
            int bucket = Utilities.selectBucket(ctx.getThreadId(),
                            ctx.getClientsInDriver(), hostPortList.size());
            NameValuePair<Integer> hostPort = hostPortList.get(bucket);
            hostURL = "http://" + hostPort.name + ':' + hostPort.value;
            loginHeaders.put("Host", hostPort.name + ':' + hostPort.value);
        }
        catch (Exception e) {
            hostURL = "http://jes-x4600-1.sfbay:8080";
            loginHeaders.put("Host", hostURL);
        }
        
        /*adding context root */
        String contextRoot = "/webapp";
               
        // hostURL = "http://" + hostPort.name + ":" + port;
        baseURL = hostURL + contextRoot;
        personDetailURL = baseURL + "/person?actionType=display_person&user_name=";
        tagSearchURL = baseURL + "/tag/display";
        tagCloudURL = baseURL + "/tag/display";
        addEventURL = baseURL + "/event/addEvent";
        //should this go to Event Detail?
        //addEventResultURL = baseURL + "/event/detail";
        addPersonURL = baseURL + "/site.jsp?page=addPerson.jsp";        
        fileUploadPersonURL = baseURL + "/api/person/fileuploadPerson";        
        fileUploadEventURL = baseURL + "/api/event/addEvent";
        
        homepageURL = baseURL + "/index.jsp";
        //change here for Action model
        logoutURL = baseURL + "/logout";
        
        loginURL = baseURL + "/person/login";
        //GET /webapp/api/event/addAttendee?id=4
        addAttendeeURL = baseURL + "/api/event/addAttendee" + "?socialEventID=";
        //updatePageURL = baseURL + "/gettextafterinsert." + type;
        eventDetailURL = baseURL + "/event/detail?socialEventID=";
        fileServiceURL = baseURL + "/access-artifacts";

        List<String[]> sList = new ArrayList<String[]>();
        sList.add(HOME_STATICS);
        homepageStatics = populateList(sList);
        sList.clear();
        sList.add(PERSON_STATICS);
        personStatics = populateList(sList);
        //personGets = populateList(PERSON_GETS);
        sList.clear();
        sList.add(TAGSEARCH_STATICS);
        tagSearchStatics = populateList(sList);
        
        if (jMakiComponentsUsed) {
            sList.clear();
            sList.add(EVENTDETAIL_COMMON_STATICS);
            sList.add(EVENTDETAIL_JMAKI_STATICS);
            eventDetailStatics = populateList(sList);
        }
        else {
            sList.clear();
            sList.add(EVENTDETAIL_COMMON_STATICS);
            sList.add(EVENTDETAIL_BASE_STATICS);
            eventDetailStatics = populateList(sList);
        }
        sList.clear();
        sList.add(ADDPERSON_STATICS);
        addPersonStatics = populateList(sList);
        sList.clear();
        sList.add(ADDEVENT_STATICS);
        addEventStatics = populateList(sList);

        loginHeaders.put("User-Agent", "Mozilla/5.0");
        loginHeaders.put("Accept", "text/xml.application/xml,application/" +
                "xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;" +
                "q=0.5");

        // We don't want the rest of the loginHeaders for cachedHeaders
		cachedHeaders = (LinkedHashMap)(loginHeaders.clone());

        loginHeaders.put("Accept-Language", "en-us,en;q=0.5");
        loginHeaders.put("Accept-Encoding", "gzip,deflate");
        loginHeaders.put("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        loginHeaders.put("Keep-Alive", "300");
        loginHeaders.put("Connection", "keep-alive");
        loginHeaders.put("Referer", homepageURL);

        isLoggedOn = false;

		// Create headers for if-modified-since
		String ifmod = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(BASE_DATE);
		cachedHeaders.put("If-Modified-Since", ifmod);
        isCached = cached();
        
        // If all the clients access EclipsElink at the same time, this may cause
        // the Exception - invalide operator: 
        //This is a work around while the problem is being investigated
        synchronized (UIDriver.class) {
            if (firstTime) {
                try {
                    http.fetchURL(homepageURL);
                } catch (IOException ex) {
                    Logger.getLogger(UIDriver.class.getName()).log(Level.SEVERE, null, ex);
                }
                firstTime = false;
            }
        }
    }

    @BenchmarkOperation (
        name    = "HomePage",
        max90th = 1,
        timing  = Timing.AUTO
    )
    public void doHomePage() throws IOException {
        logger.finer("HomePage: Accessing " + homepageURL);
        http.fetchURL(homepageURL);
        imgBytes = 0;
        imagesLoaded = 0;

        StringBuilder responseBuffer = http.getResponseBuffer();
        if (responseBuffer.length() == 0)
            throw new IOException("Received empty response");

        Set<String> images = parseImages(responseBuffer);
        //logger.info("The size of the set of images loaded is " + images.size() );
        // Fetch the CSS/JS files
        loadStatics(homepageStatics);

        loadImages(images);
        selectedEvent = RandomUtil.randomEvent(random, responseBuffer);
        validateEvent ("doHomePage", selectedEvent);
        logger.finest("Images loaded: " + imagesLoaded);
        logger.finest("Image bytes loaded: " + imgBytes);
        if (ctx.isTxSteadyState()) {
            driverMetrics.homePageImages += images.size();
            driverMetrics.homePageImagesLoaded += imagesLoaded;
            driverMetrics.homePageImageBytes += imgBytes;
        }
    }

    @BenchmarkOperation (
        name    = "Login",
        max90th = 1,
        timing  = Timing.AUTO
    )
    public void doLogin() throws IOException, Exception {
        logger.finer("In doLogin");
        int randomId = 0; //use as password
        username = null;

        if (!isLoggedOn) {
            randomId = selectUserID();
            username = UserName.getUserName(randomId);
            logger.fine("Logging in as " + username + ", " + randomId);
            http.readURL(loginURL, constructLoginPost(randomId), loginHeaders);
            // This redirects to home.
            http.fetchURL(homepageURL);

            int loginIdx = http.getResponseBuffer().indexOf("Username");
            if (loginIdx != -1){
                throw new RuntimeException("Found login prompt at index " + loginIdx + ", Login as " + username + ", " +
                                                        randomId + " failed.");
                //throw new Exception("Found login prompt at index " + loginIdx);
            }

            //logger.info("Login successful as " + username + ", " + randomId);
            // TEMP
            isLoggedOn=true;
        //if (ctx.isTxSteadyState()) ++driverMetrics.loginTotal;
        ++driverMetrics.loginTotal;
        } else {
            //already logged in --> logout,then log in again
            doLogout();
            doLogin();
        }
    }


    @BenchmarkOperation (
        name    = "Logout",
        max90th = 1,
        timing  = Timing.AUTO
    )
    public void doLogout() throws IOException {
        if (isLoggedOn){
            logger.finer("Logging off: " + username);
            http.fetchURL(logoutURL);
            cachedURLs.clear();
            isCached = cached();
            isLoggedOn=false;
            http = HttpTransport.newInstance(); // clear all state
        }
        //if (ctx.isTxSteadyState()) ++driverMetrics.logoutTotal;
        ++driverMetrics.logoutTotal;
    }


    @BenchmarkOperation (
        name    = "TagSearch",
        max90th = 2,
        timing  = Timing.AUTO
    )
    public void doTagSearch() throws IOException {
        String tag = RandomUtil.randomTagName(random);
        String post = "tag=" + tag + "&tagsearchsubmit=Submit";
        logger.finer("TagSearch: " + tagSearchURL + " Post: " + post);
        http.readURL(tagSearchURL, post);
        //if (http.getResponseCode() != 302)
        //    logger.warning("Tag search response not redirecting.");
        //for java, tagCloudURL and tagSearchURL are the same
        http.fetchURL(tagCloudURL + "?tag=" + tag);
        StringBuilder responseBuffer = http.getResponseBuffer();
        if (responseBuffer.length() == 0)
            throw new IOException("Received empty response");
        Set<String> images = parseImages(responseBuffer);
        loadStatics(tagSearchStatics);
        loadImages(images);
        String event = RandomUtil.randomEvent(random, responseBuffer);
        if (event != null) {
            selectedEvent = event;
            validateEvent("doTagSearch", selectedEvent);
        }
        if (ctx.isTxSteadyState())
            driverMetrics.tagSearchImages += images.size();
    }

    @BenchmarkOperation (
        name    = "AddEvent",
        max90th = 4,
        timing  = Timing.AUTO
    )
    @NegativeExponential (
        cycleType = CycleType.CYCLETIME,
        cycleMean = 5000,
        cycleMin = 3000,
        truncateAtMin = false,
        cycleDeviation = 2
    )
    public void doAddEvent() throws IOException {
        logger.finer("doAddEvent");
        http.readURL(addEventURL);
        loadStatics(addEventStatics);
        if(isLoggedOn) {
            // Prepare the parts for the request.
            ArrayList<Part> params = new ArrayList<Part>();
            String[] parameters = prepareEvent();
            if (parameters[0] == null || parameters[0].length() == 0)
                logger.warning("Socialevent title is null!");
            else
                logger.finer("addEvent adding event title: " + parameters[0]);

            params.add(new StringPart("title", parameters[0])); //title
            params.add(new StringPart("summary", parameters[1])); // summary
            params.add(new StringPart("description", parameters[2])); // description
            params.add(new StringPart("submitter_user_name", parameters[3]));
            params.add(new StringPart("telephone", parameters[4])); //phone
            params.add(new StringPart("timezone", parameters[5])); //timezone
            params.add(new StringPart("year", parameters[6]));
            params.add(new StringPart("month", parameters[7]));
            params.add(new StringPart("day", parameters[8]));
            params.add(new StringPart("hour", parameters[9]));
            params.add(new StringPart("minute", parameters[10]));
            params.add(new StringPart("tags", parameters[11]));
            
            //add the address
            String[] addressArr = prepareAddress();
            params.add(new StringPart("street1", addressArr[0]));
            params.add(new StringPart("street2", addressArr[1]));
            params.add(new StringPart("city", addressArr[2]));
            params.add(new StringPart("state", addressArr[3]));
            params.add(new StringPart("zip", addressArr[4]));
            params.add(new StringPart("country", addressArr[5]));

            params.add(new FilePart("upload_event_image", eventImg));
            params.add(new FilePart("upload_event_literature",eventPdf));
            params.add(new StringPart("submit", "Create"));
            /****
            Part[] parts = new Part[params.size()];
            parts = params.toArray(parts);

            PostMethod post = new PostMethod(fileUploadEventURL);
            post.setRequestEntity(
                        new MultipartRequestEntity(parts, post.getParams()));
            doMultiPartPost(post);
             ***/
            ((ApacheHC3Transport) http).readURL(fileUploadEventURL, params);
            String[] redirectLocation = http.getResponseHeader("location");

            if (redirectLocation != null) {
                http.fetchURL(baseURL + '/' + redirectLocation[0]);
            } else {
                int status = http.getResponseCode();
                if(status != HttpStatus.SC_OK)
                    throw new IOException("Multipart Post did not work, returned status code: " + status);
            }
        }
        else {
            System.out.println ("doAddEvent ==> ERROR. Not logged in. Did not amke the post call");
        }
        ++driverMetrics.addEventTotal;
    }

    @BenchmarkOperation (
        name    = "AddPerson",
        max90th = 3,
        timing  = Timing.AUTO
    )
    @NegativeExponential(
        cycleType = CycleType.CYCLETIME,
        cycleMean = 5000,
        cycleMin = 2000,
        truncateAtMin = false,
        cycleDeviation = 2
    )
    public void doAddPerson() throws IOException {
        logger.finer("doAddPerson");
        if (isLoggedOn)
            doLogout();

        http.readURL(addPersonURL);
        loadStatics(addPersonStatics);
        // Prepare the parts for the request.
        ArrayList<Part> params = new ArrayList<Part>();
        String[] parameters = preparePerson();
        
        // Debug
        if (parameters[0] == null || parameters[0].length() == 0)
            logger.warning("Username is null!");
        else
            logger.finer("addPerson adding user: " + parameters[0]);
        
        params.add(new StringPart("user_name", parameters[0]));
        params.add(new StringPart("password", parameters[1]));
        params.add(new StringPart("passwordx", parameters[1]));
        params.add(new StringPart("first_name", parameters[2]));
        params.add(new StringPart("last_name", parameters[3]));
        params.add(new StringPart("email", parameters[4]));
        params.add(new StringPart("telephone",parameters[5]));
        params.add(new StringPart("summary", parameters[6]));
        params.add(new StringPart("timezone", parameters[7]));
        String[] addressArr = prepareAddress();
        params.add(new StringPart("street1", addressArr[0]));
        params.add(new StringPart("street2", addressArr[1]));
        params.add(new StringPart("city", addressArr[2]));
        params.add(new StringPart("state", addressArr[3]));
        params.add(new StringPart("zip", addressArr[4]));
        params.add(new StringPart("country", addressArr[5]));
        params.add(new FilePart("upload_person_image", personImg));
        /****
        Part[] parts = new Part[params.size()];
        parts = params.toArray(parts);

        PostMethod post = new PostMethod(fileUploadPersonURL+"?user_name="+parameters[0]);
        post.setRequestEntity(
                        new MultipartRequestEntity(parts, post.getParams()));
        doMultiPartPost(post);
        ***/
        ((ApacheHC3Transport) http).readURL(
                fileUploadPersonURL+"?user_name="+parameters[0], params);
            String[] redirectLocation = http.getResponseHeader("location");

            if (redirectLocation != null) {
                http.fetchURL(baseURL + '/' + redirectLocation[0]);
            } else {
                int status = http.getResponseCode();
                if(status != HttpStatus.SC_OK)
                    throw new IOException("Multipart Post did not work, returned status code: " + status);
            }
        ++driverMetrics.addPersonTotal;
    }

    @BenchmarkOperation (
        name    = "EventDetail",
        max90th = 2,
        timing  = Timing.AUTO        
        )
    public void doEventDetail() throws IOException {
        //select random event
        logger.finer("doEventDetail");
        if (!validateEvent("doEventDetail", selectedEvent))
            return;
        
        http.fetchURL(eventDetailURL + selectedEvent);
        StringBuilder responseBuffer = http.getResponseBuffer();
        if (responseBuffer.length() == 0)
            throw new IOException("Received empty response");
        boolean canAddAttendee = false;
        if (!jMakiComponentsUsed) {
            canAddAttendee = isLoggedOn &&
                       responseBuffer.indexOf("Attend") != -1;
            if (!canAddAttendee && isLoggedOn) {
                if (responseBuffer.indexOf("Login:") != -1) {
                    isLoggedOn = false;
                    logger.info("Logged on as " + username + ' ' +
                        (ctx.getTime() - loginTime) +
                        " ms ago, but page shows not logged on.");
                    // logger.info(responseBuffer.toString());
                }
            }
        }
        
        Set<String> images = parseImages(responseBuffer);
	    loadStatics(eventDetailStatics);
        loadImages(images);
        // If using jmaki components, we need to make two additional calls to polulate these
        // components
        
        if (jMakiComponentsUsed) {
            //http.readURL(baseURL+"/api/event/getComments?socialEventID="+selectedEvent);
            http.fetchURL(baseURL+"/api/event/getComments?socialEventID="+selectedEvent);
            responseBuffer = http.getResponseBuffer();
            if (isLoggedOn) {
                http.fetchURL(baseURL+"/api/event/getAttendees?socialEventID="+ selectedEvent +
                        "&userName=" + username);
                responseBuffer = http.getResponseBuffer();
                // Creating the JSON object and checking the status is quite expensive
                // So using a simpler approach.
                if (responseBuffer.indexOf("not_attending") != -1 ||
                        responseBuffer.indexOf("deleted") != -1)
                    canAddAttendee = true;
            }
            else {
                http.readURL(baseURL+"/api/event/getAttendees?socialEventID="+selectedEvent +
                        "&userName=" + username);
            }
        }
        
        int card = -1;
        if (canAddAttendee) {
            // 10% of the time we can add ourselves, we will.
            card = random.random(0, 9);
            if (card == 0)
                doAddAttendee();
        }

        if (ctx.isTxSteadyState()) {
            driverMetrics.eventDetailImages += images.size();
            if (canAddAttendee) {
                ++driverMetrics.addAttendeeReadyCount;
                if (card == 0)
                    ++driverMetrics.addAttendeeCount;
            }
        }
    }

    @BenchmarkOperation (
        name = "PersonDetail",
        max90th = 2,
        timing = Timing.AUTO
    )
    public void doPersonDetail() throws IOException {
        logger.finer("doPersonDetail");
        StringBuilder buffer = new StringBuilder(fileServiceURL.length() + 20);

// TODO: account for new users when loading images, too.
        buffer.append(fileServiceURL).append("/p");
        int id = random.random(1, ScaleFactors.users);
        String userName = UserName.getUserName(id);
        //logger.info("Accessing "+personDetailURL + UserName.getUserName(id) );
        http.fetchURL(personDetailURL + userName);
        StringBuilder responseBuffer = http.getResponseBuffer();
        if (responseBuffer.length() == 0)
            throw new IOException("Received empty response");
        Set<String> images = parseImages(responseBuffer);
        loadStatics(personStatics);
		loadImages(images);
    }

    public void doAddAttendee() throws IOException {
        //can only add yourself (one attendee) to party
        
        //http.readURL(addAttendeeURL + selectedEvent);        original line before hack for session fix
        validateEvent("doAddAttendee", selectedEvent);
        http.readURL(addAttendeeURL + selectedEvent+"&userName="+ username);
        //http.readURL(updatePageURL, "list=attendees"); no need to do this - PersonRestAction sends back JSON
    }

    public Set<String> parseImages(StringBuilder buffer) {
        LinkedHashSet<String> urlSet = new LinkedHashSet<String>();
        String elStart = "<img ";
        String attrStart = " src=\"";
        int elStartLen = elStart.length() - 1; // Don't include the trailing space
        int attrStartLen = attrStart.length();
        int idx = 0;            
        for (;;) {

            // Find and copy out the element.
            idx = buffer.indexOf(elStart, idx);
            if (idx == -1)
                break;
            idx += elStartLen;
            int endIdx = buffer.indexOf("/>", idx);
            if (endIdx == -1)
                break;
            String elText = buffer.substring(idx, endIdx);
            idx = endIdx + 1;

            // Find the attribute
            int idx2 = elText.indexOf(attrStart);
            if (idx2 == -1) {
                logger.finer("No img src attribute. Weird! " + elText);
                continue;
            }
            endIdx = elText.indexOf("\"", idx2 + attrStartLen);
            if (endIdx == -1) {
                logger.warning("No img src attribute ending. Weird! " + elText);
                continue;
            }

            String link = elText.substring(idx2 + attrStartLen, endIdx);
            //logger.info("Analyzing link: " + link);

            if (link.startsWith("/webapp/access-artifacts") && link.contains("jpg")) {
                String url =  hostURL + link;
                //logger.info("url in parseImages is"+ url);
                logger.finest("Adding " + url + " from idx " + idx);
                urlSet.add(url);
            }
        }
        return urlSet;
    }

    /*
	 * We assume that the application has set an expiry far into the future.
	 * As such the browser would not re-fetch these images within the same
	 * session.
	 */
    private void loadImages(Set<String> images) throws IOException {
        if (images != null)
            for (String image : images) {
                // Loads image only if not cached, means we can add to cache.
                //logger.info("does cache contain this image " + image + ": " + cachedURLs.contains(image) );
                if (cachedURLs.add(image)) {
                    //debug statement                    
                    //logger.info("Loading image " + image);
                    if(!image.contains("jpg"))
                        logger.info("can't find image " + image);
                    int size = http.readURL(image);
                    if (size == 0)
                        logger.warning("Image: " + image + " size 0");
                    imgBytes += size;
                    ++imagesLoaded;
                } else {
                    logger.finer("Image already cached: Not loading " + image);
                    
                }
            }
    }

    private boolean cached() {
        // We have to decide whether the cache is empty or not.
        // 40% of the time, it is empty.
        boolean cached = true;
        int selector = random.random(0, 9);
        if (selector < 4) {
            cached = false;
        }
        return cached;
    }

    private void loadStatics(String[] urls) throws IOException {
        for (String url : urls) {
		// If we are simulating browser caching, send if-modified-since
		// header.
			if (isCached)
				http.readURL(url, cachedHeaders);
            else {
                if (cachedURLs.add(url)) {
                    logger.finer("Loading URL " + url);
                    http.readURL(url);
                } else {
                    logger.finer("URL already cached: Not loading " + url);
                }
            }
        }
    }

      public DateFormat getDateFormat() {
        if (df == null)
            df = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        return df;
    }

    public int selectUserID() {
        return random.random(0, ScaleFactors.USERS_RATIO - 1) *
                            ScaleFactors.activeUsers + ctx.getThreadId() + 1;
        //return random.random(0, ScaleFactors.USERS_RATIO - 1) *
        //                    ScaleFactors.Users + ctx.getThreadId() + 1;
    }


    private String[] populateList(List<String[]> stats) {
        String[] sa = new String[1];
        List<String> list = new ArrayList<String>();
        // If site statics are required, this should be added to all pages
        if (requestSiteStatics) {
            for (int i=0; i<SITE_STATICS.length; i++) {
                list.add(baseURL + SITE_STATICS[i].trim());
            }
        }
        if (stats != null) {
            for (int j=0; j<stats.size(); j++) {
                String[] sarray = stats.get(j);
                for (int i=0; i<sarray.length; i++)
                    list.add(baseURL +sarray[i].trim());
            }
            
        }
        return list.toArray(sa);
    }

    private String constructLoginPost(int randomId) {
        return "user_name=" + username + "&password=" +
                String.valueOf(randomId);
    }
    
    public void doMultiPartPost(PostMethod post) throws IOException {
        HttpClient client = ((ApacheHC3Transport) http).getHttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        // Olio uses redirect on success of add Event/Person
  	    // post.setFollowRedirects (true);
        // Need to manually follow redirects in HttpClient 3.0.1
        int status = client.executeMethod(post);
        Header locationHeader = post.getResponseHeader("location");

        if (locationHeader != null) {
            String redirectLocation = locationHeader.getValue();
            // Release the connection after we get the location, etc.
            post.releaseConnection();
            http.fetchURL(baseURL + '/' + redirectLocation);
        } else if(status != HttpStatus.SC_OK){
            post.releaseConnection();
            throw new IOException("Multipart Post did not work, returned status code: " + status);
        }
    }

    public String[] prepareEvent()  {
        String fields[]  = new String[12];
        StringBuilder buffer = new StringBuilder(256);
        
        int counter=0;
        fields[counter++] = RandomUtil.randomText(random, 15, 20); //title
        fields[counter++] = RandomUtil.randomText(random, 50, 100); // summary
        fields[counter++] = RandomUtil.randomText(random, 100, 495); // description
        fields[counter++]= UserName.getUserName(random.random(1, ScaleFactors.users));
        fields[counter++]= RandomUtil.randomPhone(random, buffer); //phone
        fields[counter++]= RandomUtil.randomTimeZone(random); // timezone
        DateFormat dateFormat = getDateFormat(); // eventtimestamp
        String strDate = dateFormat.format( 
                random.makeDateInInterval(BASE_DATE, 0, 540));                
        StringTokenizer tk = new StringTokenizer(strDate,"-");
        while (tk.hasMoreTokens()) {
            fields[counter++]=tk.nextToken();                        
        }
        //based on DateFormat,
        //fields[6]=year
        //fields[7]=month
        //fields[8]=day
        //fields[9]=hours
        //fields[10]=minutes
          int numTags = random.random(1, 7); // Avg is 4 tags per event
        for (int i = 0; i < numTags; i++)
            tagSet.add(RandomUtil.randomTagId(random, 0.1d));

        for (int tagId : tagSet)
            tags.append(UserName.getUserName(tagId)).append(' ');
        tags.setLength(tags.length() - 1);

        fields[counter]= tags.toString();
        tags.setLength(0);
        tagSet.clear();

        return fields;
    }

    public String[] prepareAddress() {

        String[] STREETEXTS = { "Blvd", "Ave", "St", "Ln", "" };
        StringBuilder buffer = new StringBuilder(255);
        buffer.append(random.makeNString(1, 5)).append(' '); // number
        RandomUtil.randomName(random, buffer, 1, 11); // street
        String streetExt = STREETEXTS[random.random(0, STREETEXTS.length - 1)];
        if (streetExt.length() > 0)
            buffer.append(' ').append(streetExt);
        String[] fields = new String[8];
        fields[0] = buffer.toString();

        int toggle = random.random(0, 1); // street2
        if (toggle > 0)
            fields[1] = random.makeCString(5, 20);
        else
            fields[1] = "";

        fields[2] = random.makeCString(4, 14); // city
        fields[3] = random.makeCString(2, 2).toUpperCase(); // state
        fields[4] = random.makeNString(5, 5);  // zip

        toggle = random.random(0, 1);
        if (toggle == 0) {
            fields[5] = "USA";
        } else {
            buffer.setLength(0);
            fields[5] = RandomUtil.randomName(random, buffer, 6, 16).toString();
        }
        // Latitude, we do not get addresses in polar circles. So the limit
       // fields[6] = String.format("%.6f", random.drandom(-66.560556d, 66.560556d));

       // fields[7] = String.format("%.6f", random.drandom(-179.999999d, 180d));

        return fields;
    }

    public String[] preparePerson() {
        String fields[]  = new String[8];
        StringBuilder b = new StringBuilder(256);
        int id = loadedUsers + personsAdded++ * ScaleFactors.activeUsers +
                                                        ctx.getThreadId() + 1;
        fields[0] = UserName.getUserName(id);
        //use the same field for repeating the password field.
        fields[1] = String.valueOf(id);
        fields[2] = RandomUtil.randomName(random, b, 2, 12).toString();
        b.setLength(0);
        fields[3] = RandomUtil.randomName(random, b, 5, 15).toString();
        fields[4] = random.makeCString(3, 10);
        fields[4] = fields[2] + '_' + fields[3] + '@' + fields[4] + ".com";
        b.setLength(0);
        fields[5] = RandomUtil.randomPhone(random, b);
        fields[6] = random.makeAString(250, 2500);
        fields[7] = RandomUtil.randomTimeZone(random);
        return fields;
    }

    static class UIDriverMetrics implements CustomMetrics {

       int addAttendeeCount = 0;
        int addAttendeeReadyCount = 0;
        int homePageImages = 0;
        int tagSearchImages = 0;
        int eventDetailImages = 0;
        int homePageImagesLoaded = 0;
        long homePageImageBytes = 0;
        int addEventTotal = 0;
        int addPersonTotal = 0;
        //additions here
        int loginTotal = 0;
        int logoutTotal = 0;

        public void add(CustomMetrics other) {
            UIDriverMetrics o = (UIDriverMetrics) other;
            addAttendeeCount += o.addAttendeeCount;
            addAttendeeReadyCount += o.addAttendeeReadyCount;
            homePageImages += o.homePageImages;
            tagSearchImages += o.tagSearchImages;
            eventDetailImages += o.eventDetailImages;
            homePageImageBytes += o.homePageImageBytes;
            homePageImagesLoaded += o.homePageImagesLoaded;
            addEventTotal += o.addEventTotal;
            addPersonTotal += o.addPersonTotal;
            //additions here
            loginTotal += o.loginTotal;
            logoutTotal += o.logoutTotal;
            
        }

               public Element[] getResults() {
            Result r = Result.getInstance();
            int total = r.getOpsCountSteady("EventDetail");
            Element[] el = new Element[15];
            el[0] = new Element();
            el[0].description = "% EventDetail views where attendee added";
            el[0].target = "&gt;= 6";
            if (total > 0) {
                double pctAdd = 100d * addAttendeeCount / (double) total;
                el[0].result = String.format("%.2f", pctAdd);
                if (pctAdd >= 6d)
                    el[0].passed = Boolean.TRUE;
                else
                    el[0].passed = Boolean.FALSE;
            } else {
                el[0].result = "";
                el[0].passed = Boolean.FALSE;
            }

            el[1] = new Element();
            el[1].description = "EventDetail count where attendee can be added";
            el[1].result = String.valueOf(addAttendeeReadyCount);

            int cnt = r.getOpsCountSteady("HomePage");
            el[2] = new Element();
            el[2].description = "Average images references on Home Page";
            el[2].target = "10";
            el[2].allowedDeviation = "0.5";
            if (cnt > 0) {
                double imagesPerPage = homePageImages / (double) cnt;
                el[2].result = String.format("%.2f", imagesPerPage);
                if (imagesPerPage >= 9.5d && imagesPerPage <= 10.5d)
                    el[2].passed = Boolean.TRUE;
                else
                    el[2].passed = Boolean.FALSE;
            } else {
                el[2].result = "";
                el[2].passed = Boolean.FALSE;
            }

            el[3] = new Element();
            el[3].description = "Average images loaded per Home Page";
            el[3].target = "&gt;= 3";
            if (cnt > 0) {
                double avgImgs = homePageImagesLoaded / (double) cnt;
                el[3].result = String.format("%.2f", avgImgs);
                if (avgImgs >= 3d)
                    el[3].passed = Boolean.TRUE;
                else
                    el[3].passed = Boolean.FALSE;
            } else {
                el[3].result = "";
                el[3].passed = Boolean.FALSE;
            }

            el[4] = new Element();
            el[4].description = "Average image bytes received per Home Page";
            el[4].target = "&gt;= 15000";
            if (cnt > 0) {
                double avgBytes = homePageImageBytes / (double) cnt;
                el[4].result = String.format("%.2f", avgBytes);
                if (avgBytes >= 15000)
                    el[4].passed = Boolean.TRUE;
                else
                    el[4].passed = Boolean.FALSE;
            } else {
                el[4].result = "";
                el[4].passed = Boolean.FALSE;
            }
            cnt = r.getOpsCountSteady("TagSearch");
            el[5] = new Element();
            el[5].description = "Average images on Tag Search Results";
            // el[5].target = "&gt;= 3.6";
            el[5].target = "&gt;= 0";
            if (cnt > 0) {
                double avgImgs = tagSearchImages / (double) cnt;
                el[5].result = String.format("%.2f", avgImgs);
                if (avgImgs >= 0)
                    el[5].passed = Boolean.TRUE;
                else
                    el[5].passed = Boolean.FALSE;
            } else {
                el[5].result = "";
                el[5].passed = Boolean.FALSE;
            }
            el[6] = new Element();
            el[6].description = "Average images on Event Detail";
            el[6].target = "&gt;= 1";
            if (total > 0) {
                double avgImgs = eventDetailImages / (double) total;
                el[6].result = String.format("%.2f", avgImgs);
                if (avgImgs >= 1d)
                    el[6].passed = Boolean.TRUE;
                else
                    el[6].passed = Boolean.FALSE;
            } else {
                el[6].result = "";
                el[6].passed = Boolean.FALSE;
            }
            el[7] = new Element();
            el[7].description = "Total successful AddEvent calls";
            el[7].result = String.valueOf(addEventTotal);
            el[8] = new Element();
            el[8].description = "Total successful AddPerson calls";
            el[8].result = String.valueOf(addPersonTotal);
            el[9] = new Element();
            el[9].description = "Concurrent user to ops/sec ratio";
            el[9].target = "&lt;= 5.25";
            double ratio = r.getScale() / r.getMetric();
            el[9].result = String.format("%.2f", ratio);
            if (ratio <= 5.25d)
                el[9].passed = true;
            else
                el[9].passed = false;
             // additions here
            //home page counts
            el[10] = new Element();
            el[10].description = "Total Home Page counts";
            el[10].target = "&gt;100";
            el[10].result = Integer.toString(r.getOpsCountSteady("HomePage"));
            el[10].passed = Boolean.TRUE;
            //home page Images Loaded
            el[11] = new Element();
            el[11].description = "Total Home Image Loaded counts";
            el[11].target = "&gt;3";
            el[11].result = Double.toString(homePageImagesLoaded);
            //home page Images Count
            el[12] = new Element();
            el[12].description = "Total Home Image counts";
            el[12].target = "&gt;3";
            el[12].result = Integer.toString(homePageImages);
            el[13] = new Element();
            el[13].description = "Total Login total";
            el[13].target = "&gt;3";
            el[13].result = Integer.toString(loginTotal);
            el[14] = new Element();
            el[14].description = "Total Logout totol";
            el[14].target = "&gt;3";
            el[14].result = Integer.toString(logoutTotal);
            return el;
        }

        public Object clone() {
            UIDriverMetrics clone = new UIDriverMetrics();
            clone.addAttendeeCount = addAttendeeCount;
            clone.addAttendeeReadyCount = addAttendeeReadyCount;
            clone.homePageImages = homePageImages;
            clone.tagSearchImages = tagSearchImages;
            clone.eventDetailImages = eventDetailImages;
            clone.homePageImageBytes = homePageImageBytes;
            clone.homePageImagesLoaded = homePageImagesLoaded;
	    clone.addEventTotal = addEventTotal;
            clone.addPersonTotal = addPersonTotal;
            clone.loginTotal = loginTotal;
            clone.logoutTotal = logoutTotal;
            return clone;
        }
    }

    private boolean validateEvent(String method, String selectedEvent) {
        if (selectedEvent == null || selectedEvent.length() == 0 || selectedEvent.equals("-1")) {
            logger.warning("method: " + method + " selectedEvent is incorrect. eventid = " + selectedEvent);
            return false;
        }
        return true;
    }
}
