package com.sv.edu.ufg.fis.amb.reproductorapp.routes

const val ROOT_MAIN_PAGE = "main"
const val ROOT_RECORD_PAGE = "record"
const val ROOT_VIDEOS_PAGE = "videos"
const val ROOT_PLAYER_PAGE = "player"

const val ARG_PLAYER_PAGE = "uri"

sealed class Routes(
    val route: String
){
    object mainpage : Routes(route = ROOT_MAIN_PAGE)
    object recordpage : Routes(route = ROOT_RECORD_PAGE)
    object videospage : Routes(route = ROOT_VIDEOS_PAGE)
    object playerpage : Routes(route = "${ROOT_PLAYER_PAGE}?uri={${ARG_PLAYER_PAGE}}")
}