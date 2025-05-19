@echo off
REM Script pour démarrer Hadoop, copier données, lancer Mahout recommender

REM Démarrer Hadoop HDFS et YARN
echo Démarrage Hadoop DFS...
start-dfs.cmd
timeout /t 5

echo Démarrage YARN...
start-yarn.cmd
timeout /t 5

REM Créer dossier HDFS et copier fichier rating.csv
echo Création dossier /user/hadoop/input dans HDFS...
hdfs dfs -mkdir -p /user/hadoop/input

echo Copie de rating.csv dans HDFS...
hdfs dfs -put -f C:\Users\Administrator\Desktop\MahoutRecommender\data\rating.csv /user/hadoop/input/

REM Lancer Mahout recommandation item-based
echo Lancement du moteur de recommandation Mahout...
mahout recommenditembased --input /user/hadoop/input/rating.csv --output /user/hadoop/output --similarityClassname SIMILARITY_COSINE --numRecommendations 5

REM Afficher les résultats
echo Résultats :
hdfs dfs -cat /user/hadoop/output/part-r-00000

pause
