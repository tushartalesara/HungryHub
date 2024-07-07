// search function for searching restaurants and items
const search=()=>{
    const searchElement = document.getElementById("search-input");
    const restaurants = document.getElementById("restaurants");
    const items = document.getElementById("items");
    const searchResults = document.getElementById("search-result");
    const city = document.getElementById("city");
    const selectedText = city.value;
    let query = searchElement.value;
    query += "_";
    query += selectedText;
    let val1 = false;
    let val2 = false;
    if(query[0]=='_') {
        searchResults.classList.remove("show");
    }else{
        let url = `http://localhost:8090/search/${query}`;

        fetch(url)
            .then((response)=>{
                return response.json();
            })
            .then((data)=>{
                if(data.length>0){
                    let text = `<div class="list-group">`;
                    text += `<p>Restaurants</p>`;
                    data.forEach((restaurant)=>{
                        text += `<p style="cursor: pointer;" id="restaurant" onclick="select(this)" class="list-group-item list-group-action search-list">${restaurant.restaurantName}</p>`;
                    })
                    text += `</div>`;
                    restaurants.innerHTML = text;
                }
                else{
                    restaurants.innerHTML = "";
                }

            })
        let itemurl = `http://localhost:8090/searchItem/${query}`;
        fetch(itemurl)
            .then((response)=>{
                return response.json();
            })
            .then((data)=>{
                if(data.length>0){
                    let text = `<div class="list-group">`;
                    text += `<p>Items</p>`;
                    data.forEach((items)=>{
                        text += `<p style="cursor: pointer;" id="item" onclick="select(this)" class="list-group-item list-group-action search-list">${items.itemName}</p>`;
                    })
                    text += `</div>`;
                    items.innerHTML = text;
                }
                else{
                    items.innerHTML = "";
                }
            })
        searchResults.classList.add("show");


    }
}

const select = (e)=>{
    const searchElement = document.getElementById("search-input");
    const restOrItem = document.getElementById("restOrItem");
    searchElement.value = e.textContent;
    if(e.id ==="restaurant"){
        restOrItem.value = "restaurant";
    }
    else{
        restOrItem.value = "item";
    }
    const searchResults = document.getElementById("search-result");
    searchResults.classList.remove("show");
}

const getRestaurant = ()=>{
    const searchElement = document.getElementById("search-input");
    const restOrItem = document.getElementById("restOrItem");
    let query = searchElement.value;
    if(restOrItem.value === "restaurant"){
        window.location.href = `http://localhost:8090/restaurants/searchRestaurant/${query}`;
    }
    else{
        window.location.href = `http://localhost:8090/restaurants/searchItem/${query}`;
    }
}

// script of profile page
function validatePasswords() {
    var newPassword = document.getElementById("userPassword").value;
    var repeatPassword = document.getElementById("repeateUserPassword").value;


    if (newPassword !== repeatPassword) {

        return false;
    } else {

        return true;
    }
}