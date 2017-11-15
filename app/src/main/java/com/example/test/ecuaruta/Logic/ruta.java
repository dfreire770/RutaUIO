package com.example.test.ecuaruta.Logic;

/**
 * Created by Mariela on 21/11/2015.
 */

    public class ruta {
        private int imagen;
        private String nombre;
        private String inicio;
        private String fin;
        public ruta(int imagen, String nombre, String inicio, String fin) {
            this.imagen = imagen;
            this.nombre = nombre;
            this.inicio= inicio;
            this.fin = fin;
        }

        public String getNombre() {
            return nombre;
        }

        public String getInicio() {
            return inicio;
        }

        public String getFin() {
            return fin;
        }

        public int getImagen() {
            return imagen;
        }
    }

