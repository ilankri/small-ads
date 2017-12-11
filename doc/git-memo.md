# Aide-mémoire Git

## Préliminaires

Pour communiquer avec le serveur Git de la fac, il faut créer une clé
SSH et l'ajouter à son compte sur le GitLab de l'UFR d'informatique.

Pour cela, il suffit se connecter à l'adresse suivante :

http://moule.informatique.univ-paris-diderot.fr:8080/profile/keys

Ensuite, il y a un lien 'generate it' qui indique comment s'y prendre.

## Commandes de base

### Commandes « locales »

#### Faire une copie locale d'un dépôt existant

```shell
git clone <repository_url>
```

Pour notre projet, la commande est :

```shell
git clone git@moule.informatique.univ-paris-diderot.fr:lankri/petites-annonces.git
```

#### Voir l'état de la copie locale du dépôt

```shell
git status
```

#### Ajouter des modifications pour la prochaine révision

```shell
git add <filename>
```

#### Créer une nouvelle révision

```shell
git commit -m <message>
```

### Commandes pour communiquer avec le serveur distant

#### Envoyer les changements vers le dépôt distant

```shell
git push
```

#### Mettre à jour la copie locale du dépôt avec le dépôt distant

```shell
git pull
```

En fait cette commande fait deux choses : d'abord, elle récupère les
changements sur le dépôt distant et ensuite elle fusionne ces
changements avec ceux de la copie locale.  Cette fusion peut être source
de conflits qu'il faut alors résoudre afin de valider la fusion.

## Autres commandes utiles

### Lister l'historique des révisions

```shell
git log
```

### Montrer les différences entre la dernière révision et la révision en cours

```shell
git diff --cached
```

## Liens utiles

https://git-scm.com/doc
