//Router:	Envuelve la aplicación y gestiona la navegación.
//Routes:	Agrupa múltiples rutas y muestra solo la primera coincidencia.
//Route:	Define una URL específica y qué componente renderizar.

//Instala lo siguiente
//npm install react-router-dom
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Clientes from "./pages/mainpages/Clientes";
import Menus from "./pages/mainpages/Menus";
import Navbar from "./pages/Navbar/Navbar";
import Default from "./pages/mainpages/Default";
import Home from "./pages/mainpages/Home";
import Estado from "./pages/mainpages/Estado";


function App() {
  return (
    <Router>
      <div className="app">
        <Navbar />
        <div className="content">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/clientes" element={<Clientes />} />
            <Route path="/menus" element={<Menus />} />
            <Route path="/estado" element={<Estado />} />
            <Route path="*" element={<Default />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;