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
 */
function overStars(starIndex,rating) {
	if (document.images) {
		for (i=1;i<=5;i++) {
			if (i <= starIndex) {
				document.images['star_' + i].src = base + "on.png";
			} else {
				document.images['star_' + i].src = base + "off.png";
			}
		}
	}
}
function outStars(starIndex,rating) {
	if (document.images) {
		for (i=1;i<=5;i++) {
			if (i <= rating) {
				document.images['star_' + i].src = base + "on.png";
			} else {
				document.images['star_' + i].src = base + "off.png";
			}
		}
	}
}

function editoverStars(starIndex,rating) {
        if (document.images) {
                for (i=1;i<=5;i++) {
                        if (i <= starIndex) {
                                document.images['editstar_' + i].src = base + "on.png";
                        } else {
                                document.images['editstar_' + i].src = base + "off.png";
                        }
                }
        }
}
function editoutStars(starIndex,rating) {
        if (document.images) {
                for (i=1;i<=5;i++) {
                        if (i <= rating) {
                                document.images['editstar_' + i].src = base +"on.jpg";
                        } else {
                                document.images['editstar_' + i].src = base +"off.jpg";
                        }
                }
        }
}

    function handleRating() {
         if (http.readyState == 4) {
             result = http.responseText;
             document.getElementById("ratingText").innerHTML=result;
         }
    }
    function rateEvent(baselink, rating) {
         http.open("GET", baselink + escape(rating), true);
         http.onreadystatechange = handleRating;
         http.send(null);
    }
    function handleEditRating() {
         if (http.readyState == 4) {
             result = http.responseText.split(",");
             document.getElementById(result[0]).innerHTML=result[1];
         }
    }
    function editrateEvent(rating,cid) {
         http.open("GET", ratinglink + escape(rating) + "&commentid=" + escape(cid) , true);
         http.onreadystatechange = handleEditRating;
         http.send(null);
    }

function ShowHideLayer(divID) {
	var box = document.getElementById(divID);	
		
	if(box.style.display == "none" || box.style.display=="") {
		box.style.display = "block"; 		
	}
	else {
		box.style.display = "none";		
	}
}











