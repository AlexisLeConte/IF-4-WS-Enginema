## TODO

- [x] Film Production Companies
- [x] Movies names

- [x] Movies produced by a Company
- [x] Movies distributed by a Company

- [x] Number of Movies produced by a Company
- [x] Movie duration
- [ ] Company Logo
- [ ] Parent Company

Rechercher capital entreprise

Rechercher filiales d�une entreprise

Rechercher acteurs d�un film

Rechercher budget d�un film

Rechercher nombre d�entr�es d�un film

Rechercher  recette d�un film

Rechercher le PDG d�une entreprise

Rechercher producteurs d�un film

Rechercher distributeurs d�un film

Rechercher compositeur musique d�un film

Rechercher genre d�un film

## Warning

DBpedia restrict the number of results per query to 10000, and the maximum offset is 40000.

## Core Resources

### Film Production Companies

These are the only companies that we will handle.

```
SELECT ?c WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
```

### Movies

These are the only movies that we will handle. They must have a known duration.

A workaround will be necessary to get all the movie names, because there are more than 100.000 movies in the database !

```
SELECT DISTINCT ?f WHERE {
  ?f rdf:type dbo:Film ;
     dbo:runtime ?r .
}
ORDER BY ?f
```

## Relationships

### Movies and the Company that produced each Movie

The Movie and the Company that made the movie (`dbp:studio`).

```
SELECT ?f ?c WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o .
  ?f rdf:type dbo:Film ;
     dbp:studio ?c .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
```

### Movies and the Company that distributed each Movie

The Movie and the Company that made each Movie accessible to the public (`dbo:distributor`).

```
SELECT ?m ?c WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o .
  ?m rdf:type dbo:Film ;
     dbo:distributor ?c .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
```

## Informations about Resources

### Number of Movies made by a Company

```
SELECT ?c (COUNT(?m) AS ?numberOfMovies) WHERE {
  ?c rdf:type dbo:Company ;
     rdf:type ?o .
  ?m rdf:type dbo:Film ;
     dbp:studio ?c .
  FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}
ORDER BY DESC(?numberOfMovies)
```

```
SELECT (COUNT(?m) AS ?numberOfMovies) WHERE {
  <http://dbpedia.org/resource/Universal_Television> rdf:type dbo:Company .
  ?m rdf:type dbo:Film ;
     dbp:studio <http://dbpedia.org/resource/Universal_Television> .
}
```

select ?entreprise (COUNT(?film) as ?NbFilms)
where
{
?entreprise rdf:type dbo:Company .
?film rdf:type dbo:Film ;
dbp:studio ?enreprise .
FILTER (?entreprise = dbr:Pixar)
}

Trouver les acteurs et producteurs et music composers d�un film
%%Exemple Cars_(film)
select ?film ?actor
where
{
?film rdf:type dbo:Film .
?film dbo:starring ?actor .
FILTER (?film = <http://dbpedia.org/resource/Cars_(film)>)
}

Trouver logo entreprise
select ?entreprise ?logo
where
{
?entreprise a dbo:Company ;
a ?o ;
dbp:logo ?logo.
FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}

Trouver entreprise parente d�une entreprise
select ?entreprise ?parent
where
{
?entreprise a dbo:Company ;
a ?o ;
dbo:parentCompany ?parent.
FILTER regex(str(?o), "WikicatFilmProductionCompaniesOf")
}

Trouver le budget d�un film
%%Exemple Cars_(film)
select ?film ?budget
where
{
?film rdf:type dbo:Film .
?film dbo:budget ?budget .
FILTER (?film = <http://dbpedia.org/resource/Cars_(film)>)
}

Trouver la dur�e (en secondes) d�un film
%%Exemple Cars_(film)
select ?film ?duration
where
{
?film rdf:type dbo:Film .
?film dbo:runtime ?duration .
FILTER (?film = <http://dbpedia.org/resource/Cars_(film)>)
}
