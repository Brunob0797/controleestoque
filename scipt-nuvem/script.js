// JavaScript Document
	let key
	let keyinv
	let quantidade
	let massa
	let price
	let producer
	let product
	let productinv
	let massadev
	var massaaux
	var massaant

var data = {
	key: key,
	massa: massa,
	price: price,
	producer: producer,
	product: product	
}

var device = {
	massadev: massadev,
}

var inventory = {
	keyinv: keyinv,
	productinv: productinv,
	quantidade: quantidade
}

var database = firebase.database();

var ref = database.ref("devices");
var products = database.ref("products");
var estoque = database.ref("inventory/device1/")

//Caso tenha alguma mudança de medida na balança
ref.on("value", function(snapshot){
	   snapshot.forEach(function(childSnapshot){
		   //Salva a mudança em uma variável
		   var device = childSnapshot.val();
		   flag = 0;
		   
		   	//Verificar o que tem de produto
			products.once("value").then(function(snapshot){
	   			snapshot.forEach(function(childSnapshot){
					//Salva massa lida em uma variável e compara com a massa anterior
					var data = childSnapshot.val();
					massaaux = device.massadev - massaant;
					
					//Compara a massa encontrada com os produtos
					if (massaaux - data.massa < 15 && massaaux - data.massa > -15 || - data.massa - massaaux < 15 && - data.massa - massaaux > -15){
						//Verifica se já existe o produto no estoque
						console.log("ok");
						estoque.once("value").then(function(snapshot){
	   						snapshot.forEach(function(childSnapshot){
								
								var inventory = childSnapshot.val();
								
								if(massaaux > 0){
									   	quantidade = inventory.quantidade + 1;
									   }
								if(massaaux < 0){
										   quantidade = inventory.quantidade - 1;
									   }
								
								//Compara a key do produto com o estoque
								if(data.key == inventory.keyinv){
									//Faz o update na quantidade do produto
									database.ref("inventory/device1/" + data.key).update({
										keyinv: data.key,
										productinv: data.product,
										quantidade: quantidade
									});
									
								}else{
									//Cria um novo produto no estoque
									quantidade = 1;
									database.ref("inventory/device1/" + data.key).set({
										keyinv: data.key,
										productinv: data.product,
										quantidade: quantidade
									});
								}
							});
						})
						
						
						
					}
						
						
		   		});
	   				
					massaant = device.massadev;
	   		})
		   
	   });
		
	   
	   })