NETWORK_NAME=docker

docker stop grafana
docker rm -f grafana
docker run -d --add-host=host.docker.internal:host-gateway --name=grafana -p 3000:3000 grafana/grafana