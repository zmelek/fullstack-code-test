const listContainer = document.querySelector('#service-list');
let servicesRequest = new Request('/service');
fetch(servicesRequest)
.then(function(response) { return response.json(); })
.then(function(serviceList) {
  serviceList.forEach(service => {
    var li = document.createElement("li");
    li.appendChild(document.createTextNode(service.name + ': ' + service.status ));
    var button = document.createElement('button');
      button.innerHTML = 'delete';
      button.onclick = function(){
        alert(JSON.stringify({url:service.name}));
        fetch('/service', {
            method: 'delete',
            headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'application/json'
            },
          body: JSON.stringify({url:service.name})
        }).then(res=> {
            location.reload()
        });
      };
    listContainer.appendChild(li);
    listContainer.appendChild(button);
  });
});

const saveButton = document.querySelector('#post-service');
saveButton.onclick = evt => {
    let urlName = document.querySelector('#url-name').value;
    let serviceName = document.querySelector('#service-name').value;
    fetch('/service', {
    method: 'post',
    headers: {
    'Accept': 'application/json, text/plain, */*',
    'Content-Type': 'application/json'
    },
  body: JSON.stringify({url:urlName,name:serviceName})
}).then(res=> location.reload());
}